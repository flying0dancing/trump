package com.lombardrisk.commons;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Leo Tu on 7/27/15.
 */
public class FileUtil extends FileUtils
{
	private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	public static int BUFFER_SIZE = 2048;
	private FileUtil(){}
	public static void ZipFiles(ArrayList<String> fileNames, String zipfile)
	{
		byte[] buf = new byte[1024];
		try
		{
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
			for (String fileNAME : fileNames)
			{
				File filehd=new File(fileNAME);
				FileInputStream in = new FileInputStream(filehd);
				out.putNextEntry(new ZipEntry(filehd.getName()));
				int len;
				while ((len = in.read(buf)) > 0)
				{
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			out.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private static List<String> unTar(InputStream inputStream, String destDir) throws Exception
	{
		List<String> fileNames = new ArrayList<String>();
		TarArchiveInputStream tarIn = new TarArchiveInputStream(inputStream, BUFFER_SIZE);
		TarArchiveEntry entry = null;
		try
		{
			while ((entry = tarIn.getNextTarEntry()) != null)
			{
				fileNames.add(entry.getName());
				if (entry.isDirectory())
				{
					createDirectory(destDir, entry.getName());
				}
				else
				{
					File tmpFile = new File(destDir + File.separator + entry.getName());
					createDirectory(tmpFile.getParent() + File.separator, null);
					OutputStream out = null;
					try
					{
						out = new FileOutputStream(tmpFile);
						int length = 0;
						byte[] b = new byte[2048];
						while ((length = tarIn.read(b)) != -1)
						{
							out.write(b, 0, length);
						}
					}
					finally
					{
						IOUtils.closeQuietly(out);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
			throw e;
		}
		finally
		{
			IOUtils.closeQuietly(tarIn);
		}

		return fileNames;
	}

	public static List<String> unTar(String tarFile, String destDir) throws Exception
	{
		File file = new File(tarFile);
		return unTar(file, destDir);
	}

	public static List<String> unTar(File tarFile, String destDir) throws Exception
	{
		if (StringUtils.isBlank(destDir))
		{
			destDir = tarFile.getParent();
		}
		destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
		return unTar(new FileInputStream(tarFile), destDir);
	}

	public static List<String> unTarBZip2(File tarFile, String destDir) throws Exception
	{
		if (StringUtils.isBlank(destDir))
		{
			destDir = tarFile.getParent();
		}
		destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
		return unTar(new BZip2CompressorInputStream(new FileInputStream(tarFile)), destDir);
	}

	public static List<String> unTarBZip2(String file, String destDir) throws Exception
	{
		File tarFile = new File(file);
		return unTarBZip2(tarFile, destDir);
	}

	public static List<String> unBZip2(String bzip2File, String destDir) throws IOException
	{
		File file = new File(bzip2File);
		return unBZip2(file, destDir);
	}

	public static List<String> unBZip2(File srcFile, String destDir) throws IOException
	{
		if (StringUtils.isBlank(destDir))
		{
			destDir = srcFile.getParent();
		}
		destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
		List<String> fileNames = new ArrayList<String>();
		InputStream is = null;
		OutputStream os = null;
		try
		{
			File destFile = new File(destDir, FilenameUtils.getBaseName(srcFile.toString()));
			fileNames.add(FilenameUtils.getBaseName(srcFile.toString()));
			is = new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(srcFile), BUFFER_SIZE));
			os = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_SIZE);
			IOUtils.copy(is, os);
		}
		finally
		{
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}
		return fileNames;
	}

	public static List<String> unGZ(String gzFile, String destDir) throws IOException
	{
		File file = new File(gzFile);
		return unGZ(file, destDir);
	}

	public static List<String> unGZ(File srcFile, String destDir) throws IOException
	{
		if (StringUtils.isBlank(destDir))
		{
			destDir = srcFile.getParent();
		}
		destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
		List<String> fileNames = new ArrayList<String>();
		InputStream is = null;
		OutputStream os = null;
		try
		{
			File destFile = new File(destDir, FilenameUtils.getBaseName(srcFile.toString()));
			fileNames.add(FilenameUtils.getBaseName(srcFile.toString()));
			is = new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(srcFile), BUFFER_SIZE));
			os = new BufferedOutputStream(new FileOutputStream(destFile), BUFFER_SIZE);
			IOUtils.copy(is, os);
		}
		finally
		{
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}
		return fileNames;
	}

	public static List<String> unTarGZ(File tarFile, String destDir) throws Exception
	{
		if (StringUtils.isBlank(destDir))
		{
			destDir = tarFile.getParent();
		}
		destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
		return unTar(new GzipCompressorInputStream(new FileInputStream(tarFile)), destDir);
	}

	public static List<String> unTarGZ(String file, String destDir) throws Exception
	{
		File tarFile = new File(file);
		return unTarGZ(tarFile, destDir);
	}

	public static void createDirectory(String outputDir, String subDir) throws Exception
	{
		File file = new File(outputDir);
		if (!(subDir == null || subDir.trim().equals("")))
		{
			file = new File(outputDir + File.separator + subDir);
		}
		if (!file.exists())
		{
			file.mkdirs();
		}
	}
	/**
	 * if folderPath existed, return true.
	 * @param folderPath
	 * @return
	 */
	public static Boolean checkDirectory(String folderPath)
	{
		Boolean flag=false;
		if(folderPath!=null){
			File folder = new File(folderPath);
			if(folder.exists()){
				flag=true;
			}
		}
		return flag;
	}
	/**
	 * find a new folder name if there has some existed folders 
	 * @author kun shen
	 * @param rootPath
	 * @param folderName
	 * @return
	 * @throws Exception
	 */
	public static String findNewNameForFolder(String rootPath,String folderName) throws Exception
	{
		String newFolderName=null;
		if(rootPath!=null && folderName!=null)
		{
			int i=1;
			newFolderName=folderName;
			while(new File(rootPath+newFolderName).isDirectory())
			{
				newFolderName=folderName+"("+String.valueOf(i)+")";
				i++;
			}
		}
		
		return newFolderName;
	}
	
	public static void deleteDirectory(String folderPath) throws Exception
	{
		if(folderPath!=null){
			File folder = new File(folderPath);
			if(folder.exists()){
				deleteDirectory(folder);
			}
		}
	}
	public static void deleteDirectory(File folder)
	{
		if(folder.isDirectory())
		{
			File[] files=folder.listFiles();
			for(int i=0;i<files.length;i++)
			{
				deleteDirectory(files[i]);
			}
		}
		if(folder.exists()){folder.delete();}
		
	}
	public static void createDirectory(String folderPath)
	{
		if(folderPath!=null)
		{
			File folder = new File(folderPath);
			if(!folder.isDirectory())
			{
				folder.mkdirs();
			}
		}
	}

	public static void copyFileToDirectory(String sourceFolder,String fileType, String destFolder) throws Exception
	{
		try
		{
			if(sourceFolder!=null && fileType!=null && destFolder!=null)
			{
				if(fileType.lastIndexOf(";")==fileType.length()-1){fileType=fileType.substring(0, fileType.length()-1);}
				String[] fileTypes=fileType.split(";");
				
				for(int i=0; i<fileTypes.length;i++)
				{
					final String fileTypeStr=fileTypes[i];
					File sourceFolderHandle=new File(sourceFolder);
					File[] files=sourceFolderHandle.listFiles(new FilenameFilter(){
			             public boolean accept(File f , String name){ 
			                 return name.endsWith(fileTypeStr);}  
			                });
					File destFolderHandle=new File(destFolder);
					for(File file:files)
					{
						copyFileToDirectory(file, destFolderHandle);
					}
				}

				
			}
		
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			logger.error(e.getMessage());
		}
		
	}
	/**
	 * copy files and folders newer than startTime
	 * @author kun shen
	 * @param sourcePath
	 * @param destPath
	 * @param startTime
	 */
	public static void copyDirectory(String sourcePath, String destPath,long startTime, long endTime)
	{
		try
		{
			if(sourcePath!=null && destPath!=null)
			{
				File sourceFile=new File(sourcePath);
				File destFile=new File(destPath);
				if(!destFile.exists()){createDirectory(destPath);}
				if(sourceFile.isDirectory())
				{
					File[] subfiles = sourceFile.listFiles();
					if(subfiles.length>0)
					{
						for(File subfile:subfiles)
						{
							if(subfile.isFile() && subfile.lastModified()>=startTime && subfile.lastModified()<=endTime)
							{
								copyFile(subfile,new File(destPath+subfile.getAbsolutePath().replace(sourcePath, "")));
							}
							if(subfile.isDirectory())
							{
								String destSubDirectory=destPath+subfile.getAbsolutePath().replace(sourcePath, "");
								copyDirectory(subfile.getAbsolutePath(),destSubDirectory,startTime, endTime);
							}
						}
					}
					
				}
				if(sourceFile.isFile() && sourceFile.lastModified()>=startTime  && sourceFile.lastModified()<=endTime)
				{
					copyFile(sourceFile,destFile);
				}
			}
		
		}catch(Exception e)
		{logger.error(e.getMessage());}

	}
	
	public static void copyDirectory(String sourcePath, String destPath)
	{
		try
		{
			if(sourcePath!=null && destPath!=null)
			{
				File sourceFile=new File(sourcePath);
				File destFile=new File(destPath);
				//IOFileFilter txtSuffixFilter = FileFilterUtils.suffixFileFilter(".txt");
				IOFileFilter notThumb=FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("Thumbs.db"));
			    IOFileFilter txtFiles = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),notThumb);//exclude Thumbs.db
			    FileFilter filter = FileFilterUtils.or(FileFilterUtils.directoryFileFilter(), txtFiles);
				copyDirectory(sourceFile,destFile,filter);
			}
		
		}catch(Exception e)
		{logger.error(e.getMessage());}

	}
	public static List<String> unZip(String zipfilePath, String destDir) throws Exception
	{
		File zipFile = new File(zipfilePath);
		if (destDir == null || destDir.equals(""))
		{
			destDir = zipFile.getParent();
		}
		destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
		ZipArchiveInputStream is = null;
		List<String> fileNames = new ArrayList<String>();

		try
		{
			is = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipfilePath), BUFFER_SIZE));
			ZipArchiveEntry entry = null;
			while ((entry = is.getNextZipEntry()) != null)
			{
				fileNames.add(entry.getName());
				if (entry.isDirectory())
				{
					File directory = new File(destDir, entry.getName());
					directory.mkdirs();
				}
				else
				{
					OutputStream os = null;
					try
					{
						os = new BufferedOutputStream(new FileOutputStream(new File(destDir, entry.getName())), BUFFER_SIZE);
						IOUtils.copy(is, os);
					}
					finally
					{
						IOUtils.closeQuietly(os);
					}
				}
			}
		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			throw e;
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}

		return fileNames;
	}

	public static List<String> unWar(String warPath, String destDir)
	{
		List<String> fileNames = new ArrayList<String>();
		File warFile = new File(warPath);
		try
		{
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(warFile));
			ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.JAR, bufferedInputStream);

			JarArchiveEntry entry = null;
			while ((entry = (JarArchiveEntry) in.getNextEntry()) != null)
			{
				fileNames.add(entry.getName());
				if (entry.isDirectory())
				{
					new File(destDir, entry.getName()).mkdir();
				}
				else
				{
					OutputStream out = new BufferedOutputStream(new FileOutputStream(new File(destDir, entry.getName())), BUFFER_SIZE);
					IOUtils.copy(in, out);
					out.close();
				}
			}
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		return fileNames;
	}

	public static List<String> unCompress(String compressFile, String destDir) throws Exception
	{
		String upperName = compressFile.toUpperCase();
		List<String> ret = null;
		if (upperName.endsWith(".ZIP"))
		{
			ret = unZip(compressFile, destDir);
		}
		else if (upperName.endsWith(".TAR"))
		{
			ret = unTar(compressFile, destDir);
		}
		else if (upperName.endsWith(".TAR.BZ2"))
		{
			ret = unTarBZip2(compressFile, destDir);
		}
		else if (upperName.endsWith(".BZ2"))
		{
			ret = unBZip2(compressFile, destDir);
		}
		else if (upperName.endsWith(".TAR.GZ"))
		{
			ret = unTarGZ(compressFile, destDir);
		}
		else if (upperName.endsWith(".GZ"))
		{
			ret = unGZ(compressFile, destDir);
		}
		else if (upperName.endsWith(".WAR"))
		{
			ret = unWar(compressFile, destDir);
		}
		return ret;
	}
	/**It will circus identifier in file searchedFileFullName and return the value behind the first searched identifier.<br>
	 * used in checkUIDisplayValue
	 * @author kun shen
	 * @param searchedFileFullName
	 * @param identifier searched string.
	 * @return the value behind the identifier.
	 * @throws Exception
	 */
	public static String serachIdentifierInRow(String searchedFileFullName, String identifier) throws Exception
	{
		String actualValue=null;
		File searchedFile=new File(searchedFileFullName);
		if(searchedFile.exists())
		{
			String searchedLine=null;
			
			FileReader fileReader=new FileReader(searchedFile);
			BufferedReader bufferReader = new BufferedReader(fileReader);
			while((searchedLine=bufferReader.readLine())!=null)
			{
				Pattern p = Pattern.compile("^"+identifier+"\""+"(.*)"+"\"$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
				Matcher m = p.matcher(searchedLine);
				if(m.find())
				{
					actualValue=m.group(1);
					break;
				}
			}
			bufferReader.close();
			fileReader.close();
		}
		return actualValue;
	}
	
	/**rewrite file's content to a new file, this method is for "Export to CSV", and the rewritten file is under the file's path, and with suffix "(exporttocsv)".
	 * @author kun shen
	 * @param fileName
	 * @return
	 */
	public static File writeToNewFile(File fileName,String suffix)
	{
		BufferedReader bufferReader=null;
		String lineStr=null;
		StringBuffer strBuffer=null;
		String newFilePath=null;
		String newFileName=fileName.getName().substring(0,fileName.getName().lastIndexOf("."))+"("+suffix+").csv";
		newFilePath=fileName.getAbsolutePath().replace(fileName.getName(), "")+newFileName;
		File newFile=new File(newFilePath);
		
		try {
			if(newFile.exists())
			{
				newFile.delete();
			}
			newFile.createNewFile();
			bufferReader=new BufferedReader(new FileReader(fileName));
			strBuffer=new StringBuffer();
			while((lineStr=bufferReader.readLine())!=null)
			{
				if(lineStr.contains("CellName")&& lineStr.contains("PageInstance")&& lineStr.contains("AdjustmentValue"))
				{
					continue;
				}
				
				lineStr=lineStr.replaceAll(",,", ",\"\",");
				lineStr=lineStr.substring(lineStr.length()-1).equals(",")?lineStr.concat("\"\""):lineStr;
				strBuffer.append(lineStr+System.getProperty("line.separator"));
				if(strBuffer.length()>5000)
				{
					//System.out.println("writeToNewFile:strBuffer:"+strBuffer.length());//
					writeContent(newFile,strBuffer.toString());
					strBuffer.setLength(0);//clear strBuffer
				}
					
				
			}
			writeContent(newFile,strBuffer.toString());
			bufferReader.close();
			
			bufferReader=null;
			strBuffer=null;
		
		} catch (IOException e) {
			logger.error(e.getMessage());
		}finally
		{
			Runtime.getRuntime().gc();
		}
		return new File(newFilePath);
	}
	
	/** find line in csv, and used in compare "export to csv" file with baseline file
	 * @author kun shen
	 * @param exportFile
	 * @param baselineStr
	 * @return
	 */
	public static String findLineInCSV(File exportFile,String baselineStr) throws Exception
	{
		String status=",\"\",\"cannot find cell\"";
		BufferedReader exportReader=null;
		String exportStr=null;
		
		try {
			exportReader=new BufferedReader(new FileReader(exportFile));
			//adding "" for empty one, like AUTHOFF,1,TEST,, -> AUTHOFF,1,TEST,"",""
			while(baselineStr.matches(".*,,.*"))
			{
				baselineStr=baselineStr.replaceAll(",,", ",\"\",");
			}
			baselineStr=baselineStr.substring(baselineStr.length()-1).equals(",")?baselineStr.concat("\"\""):baselineStr;
			//adding "" for all items in one line of a csv
			String[] baselineStrArray=null;
			baselineStrArray=baselineStr.split(",");
			List<String> newBaselineStrList=new ArrayList<String>();
			for(int i=0;i<baselineStrArray.length;i++){
				int starcount=StringUtils.countMatches(baselineStrArray[i],"\"");
				if(starcount>0){
					if(starcount%2==1){
						String temp=baselineStrArray[i];
						int j=i+1;
						for(;j<baselineStrArray.length;j++){
							int startcount2=StringUtils.countMatches(baselineStrArray[j],"\"");
							temp+=","+baselineStrArray[j];
							i++;
							if(startcount2%2==1){
								newBaselineStrList.add(temp);
								break;
							}							
						}
					}else{
						newBaselineStrList.add(baselineStrArray[i]);
					}
				}else{
					baselineStrArray[i]="\""+baselineStrArray[i]+"\"";
					newBaselineStrList.add(baselineStrArray[i]);
				}
			}
			baselineStrArray = new String[newBaselineStrList.size()];
			newBaselineStrList.toArray(baselineStrArray);


			if(baselineStrArray.length==5)
			{
				String regex=baselineStrArray[0]+",.*"+baselineStrArray[1]+",(.*),.*,"+baselineStrArray[4];
				
				Pattern pattern=Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				//long line=0;
				while((exportStr=exportReader.readLine())!=null)
				{
					int starcount=StringUtils.countMatches(exportStr,"\"");
					while(starcount%2==1){
						String tmpline=exportReader.readLine();
						if(StringUtils.isEmpty(tmpline)){continue;}
						exportStr=exportStr+tmpline;
						starcount=StringUtils.countMatches(exportStr,"\"");
					}
					//line++;
					Matcher m=pattern.matcher(exportStr);
					if(m.find())
					{
						//System.out.println(m.group(1));
						if(baselineStrArray[2].equalsIgnoreCase(m.group(1)))
						{
							status=","+m.group(1)+",\"pass\"";
						}else
						{
							status=","+m.group(1)+",\"fail\"";
						}
						break;
					}
					
				}
			}else if(baselineStrArray.length==6 && (baselineStrArray[1].equals("\"\"") || baselineStrArray[1].contains("Derived")))//TODO
			{
				String regex=baselineStrArray[0]+",.*"+baselineStrArray[2]+",(.*),.*,"+baselineStrArray[5];
				
				Pattern pattern=Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				//long line=0;
				while((exportStr=exportReader.readLine())!=null)
				{
					int starcount=StringUtils.countMatches(exportStr,"\"");
					while(starcount%2==1){
						String tmpline=exportReader.readLine();
						if(StringUtils.isEmpty(tmpline)){continue;}
						exportStr=exportStr+tmpline;
						starcount=StringUtils.countMatches(exportStr,"\"");
					}
					//line++;
					Matcher m=pattern.matcher(exportStr);
					if(m.find())
					{
						//System.out.println(m.group(1));
						if(baselineStrArray[3].equalsIgnoreCase(m.group(1)))
						{
							status=","+m.group(1)+",\"pass\"";
						}else
						{
							status=","+m.group(1)+",\"fail\"";
						}
						break;
					}
					
				}
			}
			else
			{
				status=",\"\",\"this line doesn't match format.\"";
			}
			
			exportReader.close();
		} catch (Exception e) {
			status="error:"+e.getMessage();
			logger.error(e.getMessage());
		}finally
		{
			if(exportReader!=null)
			{
				exportReader.close();
			}
		}
		return status;
	}
	
	/**
	 * if the file is existed, then write content to this existed file.
	 * @author kun shen
	 * @param file
	 * @param content
	 * @throws IOException
	 */
	public static void writeContent(File file, String content) throws IOException
	{
		if(file.exists() && StringUtils.isNotBlank(content))
		{
			FileWriter fileWriter=new FileWriter(file,true);
			fileWriter.write(content);
			fileWriter.flush();
			fileWriter.close();
		}
		
	}
	
	/**
	 * if the newFile is existed, delete it and create a empty file, then write content to this new empty file.
	 * @author kun shen
	 * @param newFile
	 * @param content
	 * @throws IOException
	 */
	public static void writeContentToEmptyFile(File newFile, String content) throws IOException
	{
		if(newFile.exists())
		{
			newFile.delete();
		}
		newFile.createNewFile();
		FileWriter fileWriter=new FileWriter(newFile);
		fileWriter.write(content);
		fileWriter.flush();
		fileWriter.close();
	}
	/**
	 * copy file from sourcePath to resultPath, if resultPath already exists fileName, add suffix like (1),(2) in fileName, and return new file name
	 * @author kun shen
	 * @param sourcePath
	 * @param resultPath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String copyToNewFile(String sourcePath,String resultPath,String fileName) throws Exception
	{
		String newFileName=fileName;
		if(new File(resultPath).isDirectory())
		{
			int count=1;
			String namePrefix=newFileName.substring(0, newFileName.lastIndexOf("."));
			String nameSuffix=newFileName.replace(namePrefix, "");
			while(new File(resultPath+newFileName).exists())
			{
				newFileName=namePrefix+"("+String.valueOf(count)+")"+nameSuffix;
				count++;
			}
		}else
		{
			createDirectory(resultPath);
		}
		if(new File(sourcePath+fileName).exists())
		{
			FileUtils.copyFile(new File(sourcePath+fileName), new File(resultPath+newFileName));
		}
		return newFileName;
	}
	/**
	 * copy file from sourcePath to resultPath, if resultPath already exists fileName, add suffix like (1),(2) in fileName, and return new file name
	 * @author kun shen
	 * @param sourcePath
	 * @param resultPath
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String copyToNewFile(String sourcePath,String resultPath,String fileName,String specialSuffix) throws Exception
	{
		String namePrefix=fileName.substring(0, fileName.lastIndexOf("."));
		String nameSuffix=fileName.replace(namePrefix, "");
		String newFileName=namePrefix+specialSuffix+nameSuffix;
		if(new File(resultPath).isDirectory())
		{
			int count=1;

			while(new File(resultPath+newFileName).exists())
			{
				newFileName=namePrefix+specialSuffix+"("+String.valueOf(count)+")"+nameSuffix;
				count++;
			}
		}else
		{
			createDirectory(resultPath);
		}
		if(new File(sourcePath+fileName).exists())
		{
			FileUtils.copyFile(new File(sourcePath+fileName), new File(resultPath+newFileName));
		}
		return newFileName;
	}
	/**
	 * rename existed file and adding suffix, return null if the file doesn't exist.
	 * @param filePath
	 * @return new file name
	 * @throws Exception
	 */
	@Deprecated
	public static String addSuffixToFile(File filePath) throws Exception
	{
		String newFileName=null;
		if(filePath.exists() && filePath.isFile())
		{
			newFileName=filePath.getName();
			int count=1;
			String namePrefix=newFileName.substring(0, newFileName.lastIndexOf("."));
			String nameSuffix=newFileName.replace(namePrefix, "");
			String path=filePath.getPath().replace(namePrefix+nameSuffix, "");
			while(new File(path+newFileName).exists())
			{
				newFileName=namePrefix+"("+String.valueOf(count)+")"+nameSuffix;
				count++;
			}
		}else
		{
			logger.info(filePath.getAbsolutePath()+" doesn't exist, no need to add suffix.");
		}

		return newFileName;
	}
	
	/**
	 * rename fileFullName with suffix, if newFilePath is a real path, the new fileFullName get it as path.  
	 * @param fileFullName
	 * @param suffix
	 * @param newFilePath if newFilePath is empty, get fileFullName's path
	 * @return return null if fileFullName doesn't exist, otherwise return new fileFullName.
	 */
	public static String createNewFileWithSuffix(String fileFullName,String suffix,String newFilePath)
	{
		String newFileName=null;
		File file=new File(fileFullName);
		if(file.exists() && file.isFile())
		{
			String fileName=file.getName();
			int count=1;
			String namePrefix=fileName.substring(0, fileName.lastIndexOf("."));
			String nameSuffix=fileName.replace(namePrefix, "");
			if(StringUtils.isBlank(newFilePath))
			{newFilePath=file.getPath().replace(namePrefix+nameSuffix, "");}
			if(StringUtils.isBlank(suffix))
			{
				newFileName=namePrefix+"("+String.valueOf(count)+")"+nameSuffix;
				while(new File(newFilePath+newFileName).exists())
				{
					count++;
					newFileName=namePrefix+"("+String.valueOf(count)+")"+nameSuffix;
				}
			}else
			{
				newFileName=namePrefix+suffix+nameSuffix;
				while(new File(newFilePath+newFileName).exists())
				{
					newFileName=namePrefix+suffix+"("+String.valueOf(count)+")"+nameSuffix;
					count++;
				}
			}
			newFileName=newFilePath+newFileName;
			logger.info("new file name is {}.",newFileName);
		}else
		{
			logger.error("argument:fileFullName[{}] doesn't exist.",fileFullName);
		}
		
		return newFileName;
	}
	/**
	 * removed content by removedByStr
	 * @param fileFullName
	 * @param newFileFullName
	 * @param removedByStr (set value to 'Export Time', this function will delete value of 'Export Time' and 'Export Time Time Zone' and all time content)
	 * @return
	 * @throws IOException
	 */
	public static void removeStrInTxt(String fileFullName,String newFileFullName,String removedByStr) throws IOException
	{
		BufferedReader exportReader=null;
		StringBuffer strBuffer=null;
		String foundStrs=null;
		try
		{
			logger.info("remove values of '"+removedByStr+"' in text");
			logger.info("from: "+fileFullName);
			logger.info("to: "+newFileFullName);
			exportReader=new BufferedReader(new FileReader(fileFullName));
			String exportStr=null;
			foundStrs="";
			int foundTime=0;
			//String regex=removedByStr;
			Pattern pattern=Pattern.compile(removedByStr, Pattern.CASE_INSENSITIVE);
			strBuffer=new StringBuffer();
			File newFile=new File(newFileFullName);
			writeContentToEmptyFile(newFile,strBuffer.toString());
			while((exportStr=exportReader.readLine())!=null)
			{
				if(StringUtils.isBlank(exportStr))continue;
				Matcher m=pattern.matcher(exportStr);
				if(m.find())
				{
					//strBuffer.append(exportStr.replace(m.group(1), "ExportTime")+System.getProperty("line.separator"));
					foundStrs+=exportStr+System.getProperty("line.separator");
					//System.out.println("~"+foundStrs+"~");
					foundTime++;
				}else
				{
					strBuffer.append(exportStr+System.getProperty("line.separator"));
				}
				if(strBuffer.length()>5000)
				{
					FileUtil.writeContent(newFile,strBuffer.toString());
					strBuffer.setLength(0);//clear strBuffer
				}
			}
			logger.info("remove lines("+foundTime+"): "+foundStrs);
			FileUtil.writeContent(newFile, strBuffer.toString());	
			strBuffer.setLength(0);//clear strBuffer
			exportReader.close();
		} catch (Exception e) {
			String status="error:"+e.getMessage();
			logger.error(status);
		}finally
		{
			if(exportReader!=null)
			{
				exportReader.close();
			}
		}
		
	}
	/**
	 * rename file
	 * @author Leo Tu
	 * @param path
	 * @param oldname
	 * @param newname
	 */
	public static String  renameFile(String path, String oldname, String newname)
	{
		String fileFullPath=null;
		Boolean flag=true;
		if (!oldname.equals(newname))
		{
			File oldfile = new File(path + System.getProperty("file.separator") + oldname);
			File newfile = new File(path + System.getProperty("file.separator") + newname);
			if (!oldfile.exists())
			{
				return null;
			}
			if (newfile.exists())
			{
				logger.warn("The file already exist, old file will be deleted");
				flag=newfile.delete();
			}
			if(flag)
			{
				if(oldfile.renameTo(newfile))
				{
					fileFullPath=newfile.getAbsolutePath();
				}
			}

		}
		else
		{
			logger.error("New file name is same with old one!");
		}
		return fileFullPath;
	}
	/**
	 * rename file
	 * @author kun shen
	 * @param oldFileFullPath
	 * @param newNameWithoutSuffix
	 */
	public static String renameFile(String oldFileFullPath, String newNameWithoutSuffix)
	{
		String newFileFullPath=null;
		File oldFile=new File(oldFileFullPath);
		if(oldFile.exists())
		{
			String fileName=oldFile.getName();
			String fileName_Prefix=fileName.substring(0, fileName.lastIndexOf("."));
			String fileName_Suffix=fileName.replace(fileName_Prefix, "");
			String filePath=oldFileFullPath.replace(fileName, "");
			if(fileName_Prefix.lastIndexOf("(")!=-1)
			{fileName_Prefix=fileName_Prefix.substring(0, fileName_Prefix.lastIndexOf("(")).trim();}
			String fileName_Prefix_Tmp=newNameWithoutSuffix+"_"+fileName_Prefix;

			newFileFullPath=filePath+fileName_Prefix_Tmp+fileName_Suffix;
			int i=1;
			while(new File(newFileFullPath).exists())
			{
				fileName_Prefix_Tmp=newNameWithoutSuffix+"_"+fileName_Prefix+"("+String.valueOf(i)+")";
				newFileFullPath=filePath+fileName_Prefix_Tmp+fileName_Suffix;
				i++;
			}
			oldFile.renameTo(new File(newFileFullPath));

		}else
		{
			logger.error("no file found "+oldFileFullPath);
		}

		return newFileFullPath;
	}

	/***
	 * get all file paths by filePath, maybe one file path return, or maybe more file paths return.
	 * @param filePath maybe contains "*"
	 */
	public static List<String> getFilesByFilter(String filePath, String excludeFilters) {
		if (StringUtils.isNotBlank(filePath)) {
			return listFilesByFilter(filePath, null, excludeFilters,false);
		}
		return new ArrayList<>();
	}

	private static Pair<File, String> splitFilePathIntoParentFileAndFileName(String filePathParam) {
		String filePath = filePathParam.replace("\"", "");
		if (filePath.endsWith("/") || filePath.endsWith("\\")) {
			filePath = filePath.substring(0, filePath.length() - 1);
		}

		int lastSlash = filePath.lastIndexOf("\\") == -1 ? filePath.lastIndexOf("/") : filePath.lastIndexOf("\\");
		File parentPath = new File(filePath.substring(0, lastSlash));
		String fileName = filePath.substring(lastSlash + 1);
		return Pair.of(parentPath, fileName);
	}

	public static List<String> listFilesByFilter(final String filePath,final String filterStr,
												  final String exfilterStr,final boolean keepDirStructure) {
		List<String> filePaths = new ArrayList<>();
		File fileFullPath = new File(filePath);
		if (fileFullPath.exists()) {
			if (fileFullPath.isDirectory()) {
				File[] files = filterFilesAndSubFolders(fileFullPath, filterStr, exfilterStr);
				Boolean flag=true;
				for (File file : files) {
					if(file.isFile()){
						if(keepDirStructure && flag){
							filePaths.add(file.getParent());
							flag=false;
						}
						filePaths.add(file.getAbsolutePath());
					}else{
						filePaths.addAll(listFilesByFilter(file.getAbsolutePath(), filterStr, exfilterStr,keepDirStructure));
					}
				}
			}else{
				filePaths.add(filePath);
			}
		} else {
			Pair<File, String> pathParts = splitFilePathIntoParentFileAndFileName(filePath);
			String fileName = pathParts.getRight();
			File parentPath = pathParts.getLeft();

			if (parentPath.isDirectory()) {
				File[] files = filterFilesAndSubFolders(parentPath, fileName, exfilterStr);
				for (File file : files) {
					if(file.isFile()){
						filePaths.add(file.getAbsolutePath());
					}else{
						filePaths.addAll(listFilesByFilter(file.getAbsolutePath(), fileName, exfilterStr,keepDirStructure));
					}
				}
			} else {
				logger.error("error: invalid path[" + filePath + "]");
			}
		}
		return filePaths;
	}

	protected static File[] filterFilesAndSubFolders(final File parentPath,final String filterStr,final String excludeFileStr) {
		final String[] filters = StringUtils.isBlank(filterStr)?null:filterStr.toLowerCase().split("\\*");
		final String[] exfilters = StringUtils.isBlank(excludeFileStr)?null:excludeFileStr.toLowerCase().replaceAll("^\\*(.*)", "$1").split(";");

		return parentPath.listFiles(new CustomFileNameFilter(filters, exfilters));
	}

	public static List<File> getFiles(String filePath, String filterStr, String exfilterStr){
		List<String> fileFullPaths=listFilesByFilter(filePath,filterStr,exfilterStr,false);
		List<File> returnFiles=new ArrayList<File>();
		for(String fileFullPath:fileFullPaths){
			returnFiles.add(new File(fileFullPath));
		}
		return returnFiles;
	}

	public static List<File> getFiles(String filePath, String filterStr, String exfilterStr,long lockerTimestamp){
		List<String> fileFullPaths=listFilesByFilter(filePath,filterStr,exfilterStr,false);
		List<File> returnFiles=new ArrayList<File>();
		File file;
		for(String fileFullPath:fileFullPaths){
			file=new File(fileFullPath);
			if(file.lastModified()>=lockerTimestamp){
				returnFiles.add(file);
			}
		}
		return returnFiles;
	}
	public static List<File> sortFileByModifiedTime(String path, String filterStr,String exfilterStr)
	{

		List<File> list = getFiles(path,  filterStr, exfilterStr);
		if (list != null && list.size() > 0)
		{
			Collections.sort(list, new Comparator<File>() {
				public int compare(File file, File newFile)
				{
					if (file.lastModified() < newFile.lastModified())
					{
						return 1;
					}
					else if (file.lastModified() == newFile.lastModified())
					{
						return 0;
					}
					else
					{
						return -1;
					}

				}
			});

		}

		return list;
	}

	/***
	 * get lasted file
	 * @param path
	 * @param filterStr
	 * @param exfileStr
	 * @param lockerTimestamp get files newer than lockerTimestamp, set -1 if does not need timestamp
	 * @return
	 */
	public static String getLatestFile(String path, String filterStr,String exfileStr,long lockerTimestamp)
	{
		List<File> list;
		if(lockerTimestamp<=0){
			list = getFiles(path,  filterStr, exfileStr);
		}else{
			list = getFiles(path,  filterStr, exfileStr,lockerTimestamp);
		}
		try
		{
			if(list!=null && list.size()>0){
				HashMap<Long,String> timeFileMap=new HashMap<Long, String>();
				File file=list.get(0);;
				long flagTimestamp=file.lastModified();
				timeFileMap.put(flagTimestamp,file.toString());
				for(int i=1;i<list.size();i++){
					file=list.get(i);
					if(file.lastModified()>flagTimestamp){
						flagTimestamp=file.lastModified();
						timeFileMap.put(flagTimestamp,file.toString());
					}
				}
				return timeFileMap.get(flagTimestamp);
			}
		}
		catch (Exception e)
		{
			return "";
		}
		return "";
	}
	private static class CustomFileNameFilter implements FilenameFilter {
		private static final String regex="\\*";
		private final String[] filters;
		private final String[] exFilters;

		public CustomFileNameFilter(final String[] filters,final String[] exFilters){

			this.filters = filters;
			this.exFilters = exFilters;
		}

		@Override
		public boolean accept(final File dir,final String name) {
			if (new File(dir, name).isDirectory() && !name.startsWith(".")) {
				return true;
			}
			if(ArrayUtils.isNotEmpty(filters)){
				for (String filter : filters) {
					if (StringUtils.isNotBlank(filter) && !name.toLowerCase().contains(filter)) {
						return false;
					}
				}
			}
			return runExcludeFilters(name);
		}

		private boolean runExcludeFilters(final String name) {
			if(ArrayUtils.isNotEmpty(exFilters)){
				for (String exfilter : exFilters) {
					boolean exflag = false;
					if (StringUtils.isNotBlank(exfilter)) {
						exflag = true;
						String[] subfilters = exfilter.split(regex);
						for (String subexfilter : subfilters) {
							if (StringUtils.isNotBlank(subexfilter) && !name.toLowerCase().contains(subexfilter)) {
								exflag = false;
								break;
							}
						}
					}
					if (exflag) {
						return false;
					}
				}
			}
			return true;
		}
	}
}
