package com.fortunes.zxcx.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import android.util.Log;

/**
 * 压缩工具类
 * 
 * @author zwr
 * 
 */
public final class ZipUtils {
	private ZipUtils() {
	}

	/**
	 * 取得压缩包中的 文件列表(文件夹,文件自选)
	 * 
	 * @param zipFileString
	 *            压缩包名字
	 * @param bContainFolder
	 *            是否包括 文件夹
	 * @param bContainFile
	 *            是否包括 文件
	 * @return
	 * @throws Exception
	 */
	public static java.util.List<File> getFileList(
			String zipFileString, boolean bContainFolder, boolean bContainFile)
			throws Exception {
		java.util.List<File> fileList = new java.util.ArrayList<File>();
		java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(
				new java.io.FileInputStream(zipFileString));
		java.util.zip.ZipEntry zipEntry;
		String szName = "";
		while ((zipEntry = inZip.getNextEntry()) != null) {
			szName = zipEntry.getName();
			if (zipEntry.isDirectory()) {
				// get the folder name of the widget
				szName = szName.substring(0, szName.length() - 1);
				File folder = new File(szName);
				if (bContainFolder) {
					fileList.add(folder);
				}
			} else {
				File file = new File(szName);
				if (bContainFile) {
					fileList.add(file);
				}
			}
		}// end of while
		inZip.close();
		return fileList;
	}

	/**
	 * 返回压缩包中的文件InputStream
	 * 
	 * @param zipFilePath
	 *            压缩文件的名字
	 * @param fileString
	 *            解压文件的名字
	 * @return InputStream
	 * @throws Exception
	 */
	public static InputStream upZip(String zipFilePath,
			String fileString) throws Exception {
		java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(zipFilePath);
		java.util.zip.ZipEntry zipEntry = zipFile.getEntry(fileString);

		return zipFile.getInputStream(zipEntry);
	}

	/**
	 * 解压一个压缩文档 到指定位置
	 * 
	 * @param input
	 *            压缩包的
	 * @param outPathString
	 *            指定的路径
	 * @throws Exception
	 */
	public static void unZipFolder(InputStream input, String outPathString)
			throws Exception {
		java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(
				input);
		java.util.zip.ZipEntry zipEntry = null;
		String szName = "";

		while ((zipEntry = inZip.getNextEntry()) != null) {
			szName = zipEntry.getName();
			File f = new File(outPathString);
			if (!f.exists()) {
				f.mkdir();
			}
			if (zipEntry.isDirectory()) {
				// get the folder name of the widget
				szName = szName.substring(0, szName.length() - 1);
				File folder = new File(outPathString
						+ File.separator + szName);
				folder.mkdirs();
			} else {
				File file = new File(outPathString
						+ File.separator + szName);
				file.createNewFile();
				// get the output stream of the file
				FileOutputStream out = new FileOutputStream(
						file);
				int len;
				byte[] buffer = new byte[1024];
				// read (len) bytes into buffer
				while ((len = inZip.read(buffer)) != -1) {
					// write (len) byte from buffer at the position 0
					out.write(buffer, 0, len);
					out.flush();
				}
				out.close();
			}
		}// end of while
		inZip.close();
	}

	/**
	 * 解压一个压缩文档 到指定位置
	 * 
	 * @param zipFileString
	 *            压缩包的名字
	 * @param outPathString
	 *            指定的路径
	 * @throws Exception
	 */
	public static void unZipFolder(String zipFileString, String outPathString)
			throws Exception {
		unZipFolder(new java.io.FileInputStream(zipFileString), outPathString);
	}// end of func

	/**
	 * 解压一个压缩文档 到指定位置 支持中文
	 * 
	 * @param zipFileString
	 *            压缩包的路径
	 * @param outPathString
	 *            指定输出的路径
	 * @throws Exception
	 */
	public static String unZipFileCN(String zipFileString, String outPathString)
			throws IOException, FileNotFoundException, ZipException {
		if (!new File(zipFileString).exists()) {
			return "";
		}
		BufferedInputStream bi;
		String htmlurl = null;
		ZipFile zf = new ZipFile(zipFileString, "GBK");
		Enumeration e = zf.getEntries();
		while (e.hasMoreElements()) {
			ZipEntry ze2 = (ZipEntry) e.nextElement();
			String entryName = ze2.getName();
			String path = outPathString + "/" + entryName;
			if (ze2.isDirectory()) {
				// System.out.println("正在创建解压目录 - " + entryName);
				File decompressDirFile = new File(path);
				if (!decompressDirFile.exists()) {
					decompressDirFile.mkdirs();
				}
			} else {
				// System.out.println("正在创建解压文件 - " + entryName);
				String fileDir = path.substring(0, path.lastIndexOf("/"));
				htmlurl = outPathString + "/" + entryName;
				File fileDirFile = new File(fileDir);
				if (!fileDirFile.exists()) {
					fileDirFile.mkdirs();
				}
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(outPathString + "/" + entryName));
				bi = new BufferedInputStream(zf.getInputStream(ze2));
				byte[] readContent = new byte[1024];
				int readCount = bi.read(readContent);
				while (readCount != -1) {
					bos.write(readContent, 0, readCount);
					readCount = bi.read(readContent);
				}
				bos.close();
			}
		}
		zf.close();
		// bIsUnzipFinsh = true;
		return htmlurl;
	}

	/**
	 * 压缩文件,文件夹
	 * 
	 * @param srcFilePath
	 *            要压缩的文件/文件夹名字
	 * @param zipFilePath
	 *            指定压缩的目的和名字
	 * @throws Exception
	 */
	public static void zipFolder(String srcFilePath, String zipFilePath)
			throws Exception {
		Log.i("zipsrcFilePath", srcFilePath);
		Log.i("zipFilePath", zipFilePath);

		// 创建Zip包
		java.util.zip.ZipOutputStream outZip = new java.util.zip.ZipOutputStream(
				new FileOutputStream(zipFilePath));

		// 打开要输出的文件
		File file = new File(srcFilePath);

		File[] files = file.listFiles();

		for (int i = 0; i < files.length; i++) {
			// 压缩
			zipFiles(files[i].getParent() + File.separator,
					files[i].getName(), outZip);
		}
		// 完成,关闭
		outZip.finish();
		outZip.close();

	}// end of func

	/**
	 * 压缩文件
	 * 
	 * @param folderPath
	 * @param filePath
	 * @param zipOut
	 * @throws Exception
	 */
	private static void zipFiles(String folderPath, String filePath,
			java.util.zip.ZipOutputStream zipOut) throws Exception {
		Log.i("sdfasdfsadfsfsafsadfasdfasfsad", folderPath);
		Log.i("asdfasdfsadsadfsadfsadfsadfsadfsad", filePath);
		if (zipOut == null) {
			return;
		}

		File file = new File(folderPath + filePath);

		// 判断是不是文件
		if (file.isFile()) {
			java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(
					filePath);
			java.io.FileInputStream inputStream = new java.io.FileInputStream(
					file);
			zipOut.putNextEntry(zipEntry);

			int len;
			byte[] buffer = new byte[4096];

			while ((len = inputStream.read(buffer)) != -1) {
				zipOut.write(buffer, 0, len);
			}

			zipOut.closeEntry();
		} else {
			// 文件夹的方式,获取文件夹下的子文件
			String fileList[] = file.list();

			// 如果没有子文件, 则添加进去即可
			if (fileList.length <= 0) {
				java.util.zip.ZipEntry zipEntry = new java.util.zip.ZipEntry(
						filePath + File.separator);
				zipOut.putNextEntry(zipEntry);
				zipOut.closeEntry();
			} else {
				// 如果有子文件, 遍历子文件
				for (int i = 0; i < fileList.length; i++) {
					Log.i("sdafsdafsdafds", fileList[i]);
					// 去掉文件夹
					if (fileList[i].contains(".")) {
						zipFiles(folderPath, filePath + File.separator
								+ fileList[i], zipOut);
					}
				}// end of for
			}

		}// end of if

	}// end of func

}
