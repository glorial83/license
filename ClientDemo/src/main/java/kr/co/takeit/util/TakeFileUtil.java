package kr.co.takeit.util;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TakeFileUtil {

	private static final Logger log = LoggerFactory.getLogger( TakeFileUtil.class );

	/**
	 * 경로 재설정
	 *
	 * @param path
	 * @return
	 */
	public static String generalizePath( String path )
	{
		// int lp=0;
		StringBuffer sb = new StringBuffer();
		int l = path.length();
		for(int i=0;i<l;i++) {
			char ch = path.charAt(i);
			if(ch=='\\'||ch=='/') {
				sb.append('/');
				while(i<l-1 && (path.charAt(i+1)=='\\'||path.charAt(i+1)=='/'))
					i++;
			}
			else
				sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * 배열로 정의된 경로 재설정
	 *
	 * @param paths
	 */
	public static void generalizePath( String[] paths )
	{
		for(int i=0;i<paths.length;i++)
			paths[i] = generalizePath(paths[i]);
	}

	/**
	 * 다운로드 처리
	 *
	 * @param request
	 * @param response
	 * @param filePath
	 * @param downName
	 * @throws IOException
	 */
	public static void download(HttpServletRequest request, HttpServletResponse response, String filePath, String downName) throws IOException{
		response.reset();
		File f = new File(filePath);
		long len = f.length();

		String browser = request.getHeader("User-Agent"); //파일 인코딩
		if(browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Chrome")){
			downName = URLEncoder.encode(downName, "UTF-8");
		}else{
			downName = new String(downName.getBytes("UTF-8"), "ISO-8859-1");
		}
		downName = downName.replaceAll("\\+", " ");

		response.setHeader("Content-Type", "application/unknown");
		response.addHeader("content-disposition","attachment;filename=\""+downName+ "\"");
		response.addHeader("Content-Length", String.valueOf(len));
		ServletOutputStream os = null;

		try{
			os = response.getOutputStream();
			writeFile(generalizePath(filePath),os);
			os.flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			os.close();
		}
	}

	/**
	 * 해당 경로에 폴더가 없을 경우 생성 처리
	 *
	 * @param dir
	 * @throws IOException
	 */
	public static void ensureDirectory( String dir ) throws IOException
	{
		File f = new File( generalizePath(dir) );
		if(!f.isDirectory())
			if(!f.mkdirs())
				throw new IOException("[electria] directory creation failure");
	}

	/**
	 * OutputStream을 통해 파일 생성
	 *
	 * @param file
	 * @param os
	 * @throws IOException
	 */
	public static void writeFile( File file, OutputStream os ) throws IOException
	{
		FileInputStream fs = new FileInputStream( file );
		int no;
		byte[] buffer=new byte[65536];
		while((no = fs.read(buffer))>0)
			os.write(buffer,0,no);
		fs.close();
	}

	/**
	 * OutputStream을 통해 파일 생성
	 *
	 * @param fileName
	 * @param os
	 * @throws IOException
	 */
	public static void writeFile( String fileName, OutputStream os ) throws IOException
	{
		FileInputStream fs = new FileInputStream( generalizePath(fileName) );
		int no;
		byte[] buffer=new byte[65536];
		while((no = fs.read(buffer))>0)
			os.write(buffer,0,no);
		fs.close();
	}

	/**
	 * Binary로 파일 생성
	 *
	 * @param fileName
	 * @param buffer
	 * @throws IOException
	 */
	public static void writeBinary( String fileName, byte[] buffer ) throws IOException
	{
		File f = new File(generalizePath(fileName));
		FileOutputStream fis = new FileOutputStream( f );

		DataOutputStream din = new DataOutputStream(fis);
		din.write(buffer);
		din.flush();
		din.close();
		fis.close();
	}

	/**
	 * 문자열로 파일 생성
	 *
	 * @param fileName
	 * @param doc
	 * @throws IOException
	 */
	public static void writeText( String fileName, String doc ) throws IOException
	{
		writeText(fileName,false, doc);
	}


	/**
	 * 문자열로 파일 생성 및 기존파일에 문자열 추가
	 *
	 * @param fileName
	 * @param isAppend
	 * @param doc
	 * @throws IOException
	 */
	public static void writeText( String fileName, boolean isAppend, String doc  ) throws IOException
	{
		BufferedWriter wr = new BufferedWriter( new FileWriter(generalizePath(fileName), isAppend ));

		wr.write( doc, 0, doc.length());
		wr.flush();
		wr.close();
	}

	/**
	 * 이미지 리사이즈하여 byte로 반환
	 *
	 * @param fileName
	 * @param width
	 * @param height
	 * @param isFix
	 * @return
	 * @throws IOException
	 */
	public static byte[] readResizeBinary( String fileName, int width, int height, boolean isFix ) throws IOException
	{
		File f = new File(generalizePath(fileName));
		File resizeFile = resize(f, width, height, isFix);

		FileInputStream fis = new FileInputStream( resizeFile );
		byte[] buffer = new byte[(int)resizeFile.length()];

		DataInputStream din = new DataInputStream(fis);
		for(int i=0;i<buffer.length;i++)
			buffer[i] = (byte)din.read();
		din.close();
		fis.close();
		return buffer;
	}

	/**
	 * 이미지 리사이즈하여 byte로 반환
	 *
	 * @param fileName
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public static byte[] readResizeBinary( String fileName, int width, int height ) throws IOException
	{
		return readResizeBinary(fileName, width, height, false);
	}

	/**
	 * 이미지 리사이즈하여 byte로 반환
	 *
	 * @param file
	 * @param width
	 * @param height
	 * @return
	 * @throws IOException
	 */
	public static byte[] readResizeBinary( File file, int width, int height ) throws IOException
	{
		return readResizeBinary( file, width, height, false);
	}

	/**
	 * 이미지 리사이즈하여 byte로 반환
	 *
	 * @param file
	 * @param width
	 * @param height
	 * @param isFix
	 * @return
	 * @throws IOException
	 */
	public static byte[] readResizeBinary( File file, int width, int height, boolean isFix ) throws IOException
	{
		File resizeFile = resize(file, width, height, isFix);

		FileInputStream fis = new FileInputStream( resizeFile );
		byte[] buffer = new byte[(int)resizeFile.length()];

		DataInputStream din = new DataInputStream(fis);
		for(int i=0;i<buffer.length;i++)
			buffer[i] = (byte)din.read();
		din.close();
		fis.close();
		return buffer;
	}

	/**
	 * 파일을 읽어 byte 배열로 반환
	 *
	 * @param fileName 파일이름
	 * @return byte배열
	 * @throws IOException
	 */
	public static byte[] readBinary( String fileName ) throws IOException
	{
		File f = new File(generalizePath(fileName));
		FileInputStream fis = null;
		byte[] buffer = new byte[(int)f.length()];

		DataInputStream din = null;

		try{
			fis = new FileInputStream( f );
			din = new DataInputStream(fis);

			for(int i=0;i<buffer.length;i++){
				buffer[i] = (byte)din.read();
			}
		}catch(Exception ex){
			log.error(ex.getMessage());
		}finally{
			if( din != null ) din.close();
			if( fis != null ) fis.close();
		}

		return buffer;
	}

	/**
	 * 파일을 읽어 byte배열로 변환
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBinary( File file ) throws IOException
	{
		FileInputStream fis = null;
		byte[] buffer = new byte[(int)file.length()];

		DataInputStream din = null;

		try{
			fis = new FileInputStream( file );
			din = new DataInputStream( fis );

			for(int i=0;i<buffer.length;i++){
				buffer[i] = (byte)din.read();
			}
		}catch(Exception ex){
			log.error(ex.getMessage());
		}finally{
			if( din != null ) din.close();
			if( fis != null ) fis.close();
		}

		return buffer;
	}

	/**
	 * 파일을 읽어서 문자열로 반환
	 *
	 * @param fileName 파일이름
	 * @return 파일내용
	 * @throws IOException
	 */
	public static String readText( String fileName ) throws IOException
	{
		BufferedReader br = new BufferedReader( new FileReader(generalizePath(fileName)));

		String s;
		StringBuffer sb=new StringBuffer();
		while((s=br.readLine())!=null) {
			sb.append(s).append('\n');

		}
		br.close();
		return sb.toString();
		// return new String(sb.toString().getBytes("8859_1"),"euc-kr");
	}

	public static String readText( String fileName, String encType ) throws IOException
	{
		BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream(fileName), encType));

		String s;
		StringBuffer sb=new StringBuffer();
		while((s=br.readLine())!=null) {
			sb.append(s).append('\n');

		}
		br.close();
		return sb.toString();
		// return new String(sb.toString().getBytes("8859_1"),"euc-kr");
	}

	/**
	 * 해당 경로에 있는 파일을 읽어 ArrayList에 저장
	 *
	 * @param path
	 * @param al
	 * @param scanSubDir
	 */
	private static void listAllFiles( String path, ArrayList<String> al, boolean scanSubDir )
	{
		File f = new File(generalizePath(path));
		File[] allf = f.listFiles();
		if(allf==null)
			return;
		for(int i=0;i<allf.length;i++) {
			File cf = allf[i];
			if(cf.isDirectory()) {
				if(scanSubDir)
					listAllFiles(path+cf.getName()+"\\",al, true);
			}
			else {
				al.add( path+cf.getName() );
			}
		}
	}

	/**
	 * 해당 경로에 있는 파일을 읽어 ArrayList로 반환
	 *
	 * @param path
	 * @param scanSubDir
	 * @return
	 */
	public static ArrayList<String> listAllFiles( String path, boolean scanSubDir )
	{
		int lc =path.charAt(path.length()-1);
		if(lc!='\\' && lc!='/')
			path=path+"\\";

		ArrayList<String> al = new ArrayList<String>();
		listAllFiles( path, al, scanSubDir);
		return al;
	}

	/**
	 * 이미지 리사이즈 처리후 다운로드 처리 (100 X 100으로 지정)
	 *
	 * @param request
	 * @param response
	 * @param filePath
	 * @param downName
	 * @throws IOException
	 */
	public static void downloadResize(HttpServletRequest request, HttpServletResponse response, String filePath, String downName) throws IOException{
		downloadResize(request, response, filePath, downName, 100, 100);
	}

	/**
	 * 이미지 리사이즈 처리후 다운로드 처리
	 *
	 * @param request
	 * @param response
	 * @param filePath
	 * @param downName
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	public static void downloadResize(HttpServletRequest request, HttpServletResponse response, String filePath, String downName, int width, int height) throws IOException{
		downloadResize(request, response, filePath, downName,  width, height, false);
	}

	/**
	 * 이미지 리사이즈 처리후 다운로드 처리
	 *
	 * @param request
	 * @param response
	 * @param filePath
	 * @param downName
	 * @param width
	 * @param height
	 * @param isFix
	 * @throws IOException
	 */
	public static void downloadResize(HttpServletRequest request, HttpServletResponse response, String filePath, String downName, int width, int height, boolean isFix) throws IOException{
		response.reset();

		File file = new File(filePath);
		File outFile = resize(file, width, height, isFix);

		String browser = request.getHeader("User-Agent"); //파일 인코딩
		if(browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Chrome")){
			downName = URLEncoder.encode(downName, "UTF-8");
		}else{
			downName = new String(downName.getBytes("UTF-8"), "ISO-8859-1");
		}
		downName = downName.replaceAll("\\+", " ");

		long len = outFile.length();
		response.setHeader("Content-Type", "application/unknown");
		response.addHeader("content-disposition","attachment;filename="+downName);
		response.addHeader("Content-Length", String.valueOf(len));
		ServletOutputStream os = response.getOutputStream();
		writeFile(outFile, os);
		os.close();
	}

	/**
	 * 이미지 리사이즈 처리
	 *
	 * @param file
	 * @param width
	 * @param height
	 * @param isFix
	 * @return
	 * @throws IOException
	 */
	public static File resize(File file, int width, int height, boolean isFix) throws IOException{
		File outFile = File.createTempFile("img", null);
		String ext = getFileExtension(file.getName()).substring(1);

		int scaledWidth = 0, scaledHeight = 0;

	    BufferedImage img = ImageIO.read( file );

if( isFix ){
	scaledWidth = width;
	scaledHeight = height;
}else{
	    scaledWidth = width;
	    scaledHeight = (int) (img.getHeight() * ( (double) scaledWidth / img.getWidth() ));

	    if (scaledHeight> height) {
	        scaledHeight = height;
	        scaledWidth= (int) (img.getWidth() * ( (double) scaledHeight/ img.getHeight() ));

	        if (scaledWidth > width) {
	            scaledWidth = width;
	            scaledHeight = height;
	        }
	    }
}

	    //Image resized =  img.getScaledInstance( scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
	    //BufferedImage buffered = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
	    //buffered.getGraphics().drawImage(resized, 0, 0 , null);

	    int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
	    //BufferedImage bi = new BufferedImage(scaledWidth, scaledHeight, type);
	    //Graphics2D graphics = bi.createGraphics();
	    //graphics.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
	    //graphics.dispose();

	    BufferedImage resizedImage = new BufferedImage(scaledWidth, scaledHeight, type);
	    Graphics2D g = resizedImage.createGraphics();
	    g.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
	    g.dispose();
	    g.setComposite(AlphaComposite.Src);

	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION	, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.setRenderingHint(RenderingHints.KEY_RENDERING		, RenderingHints.VALUE_RENDER_QUALITY);
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING	, RenderingHints.VALUE_ANTIALIAS_ON);

	    ImageIO.write(resizedImage, ext, outFile);


//File input = new File("D:/compress.jpg");
//BufferedImage image = ImageIO.read(outFile);

//File compressFile = new File("D:/compress.jpg");;
//OutputStream os =new FileOutputStream(compressFile);

//Iterator<ImageWriter>writers =  ImageIO.getImageWritersByFormatName(ext);
//ImageWriter writer = (ImageWriter) writers.next();

//ImageOutputStream ios = ImageIO.createImageOutputStream(os);
//writer.setOutput(ios);

//ImageWriteParam param = writer.getDefaultWriteParam();

//param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//param.setCompressionQuality(0.5f);
//writer.write(null, new IIOImage(image, null, null), param);


	    return outFile;
//return compressFile;
	}

	/**
	 * 파일의 확장자명을 반환
	 *
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension( String fileName )
	{
		String ext = "";
		if(fileName!=null) {
			int p = fileName.lastIndexOf('.');
			if(p>=0) {
				ext = fileName.substring(p).toLowerCase();
			}
		}

		return ext;
	}

	/**
	 * 파일 존재여부를 반환
	 *
	 * @param file
	 * @return
	 */
	public static boolean isExist( File file )
	{
		boolean isExist = false;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			isExist = fis != null;
		} catch (FileNotFoundException ex) {
			log.error(ex.getMessage());
		} finally {
			if( fis != null ) {
				try{ fis.close(); } catch(Exception e){}
			}
		}

		return isExist;
	}

	/**
	 * 파일 삭제 처리
	 *
	 * @param file
	 * @throws IOException
	 */
	public static void deleteFile( File file ) throws IOException
	{
		if(file != null ){
			file.delete();
		}
	}

	/**
	 * 폴더안의 파일 삭제 처리
	 *
	 * @param dir
	 * @throws IOException
	 */
	public static void deleteDirectory( String dir ) throws IOException
	{
		deleteDirectory(dir, true);
	}

	/**
	 * 폴더안의 파일 삭제 후 폴더 삭제여부 판단
	 *
	 * @param dir
	 * @param directoryDelete
	 * @throws IOException
	 */
	public static void deleteDirectory( String dir, boolean directoryDelete ) throws IOException
	{
		File f = new File( generalizePath(dir) );

		if(f!=null && f.isDirectory()) {
			File[] list = f.listFiles();
			if(list!=null)
				for(int i=0;i<list.length;i++) {
					if(list[i].isDirectory())
						//deleteDirectory(dir+"\\"+list[i].getName());
						deleteDirectory(dir+"/"+list[i].getName());
					list[i].delete();
				}

			if( directoryDelete ){
				f.delete();
			}
		}
	}

	/**
	 * 파일의 이미지 여부 (true/false)
	 *
	 * @param ext
	 * @return
	 */
	public static boolean isPhoto(String ext){
		String photoExt = ".PNG,.JPG,.JPEG,.GIF,.TIF,.BMP";
		return photoExt.indexOf(ext.toUpperCase()) >= 0;
	}

	/**
	 * 동영상 캡쳐 (ffmpeg 이용)
	 *
	 * @param ffmpegPath
	 * @param mediaFile
	 * @param captureFile
	 */
	public static void mediaCapture(String ffmpegPath, String mediaFile, String captureFile){
		Runtime run = Runtime.getRuntime();
		String command = ffmpegPath +" -i "+mediaFile+" -r 1 " + captureFile;

		try{
			run.exec("cmd exe chcp 65001");
			run.exec(command);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * 이미지 합치기
	 *
	 * @param fileName
	 * @param overayFile
	 * @return
	 * @throws IOException
	 */
	public static byte[] readCombineBinary( String fileName, String overayFile ) throws IOException
	{
		File f = combine(fileName, overayFile);
		FileInputStream fis = null;
		byte[] buffer = new byte[(int)f.length()];

		DataInputStream din = null;

		try{
			fis = new FileInputStream( f );
			din = new DataInputStream(fis);

			for(int i=0;i<buffer.length;i++){
				buffer[i] = (byte)din.read();
			}
		}catch(Exception ex){
			log.error(ex.getMessage());
		}finally{
			if( din != null ) din.close();
			if( fis != null ) fis.close();
		}

		return buffer;
	}

	/**
	 * 이미지 리사이즈 후 합치기
	 *
	 * @param fileName
	 * @param width
	 * @param height
	 * @param isFix
	 * @param overayFile
	 * @return
	 * @throws IOException
	 */
	public static byte[] readResizeCombineBinary( String fileName, int width, int height, boolean isFix, String overayFile ) throws IOException
	{
		File f = new File(generalizePath(fileName));
		File resizeFile = resize(f, width, height, isFix);
		File combinFile = combine(resizeFile.getAbsolutePath(), overayFile);

		FileInputStream fis = new FileInputStream( combinFile );
		byte[] buffer = new byte[(int)combinFile.length()];

		DataInputStream din = new DataInputStream(fis);
		for(int i=0;i<buffer.length;i++)
			buffer[i] = (byte)din.read();
		din.close();
		fis.close();
		return buffer;
	}

	/**
	 * 이미지 합차기
	 *
	 * @param imageFile
	 * @param overayFile
	 * @return
	 */
	public static File combine(String imageFile, String overayFile){
		try {
			BufferedImage image 	= ImageIO.read(new File(imageFile));		/* 배경 이미지 */
			BufferedImage overlay 	= ImageIO.read(new File(overayFile));		/* 덮어씌울 이미지 */

			int w = Math.max(image.getWidth(), overlay.getWidth());
			int h = Math.max(image.getHeight(), overlay.getHeight());

			BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

			// 메모리 이미지에서 Graphics2D를 얻어옴
			Graphics g = combined.getGraphics();
			// 메모리 이미지에 그림을 그리자
			g.drawImage(image, 0, 0, null);
			g.drawImage(overlay, 0, 0, null);
			// 메모리 이미지를 파일로 저장한다.
			File file = new File(imageFile.replace(".png", "_combine.png"));
			boolean ok = ImageIO.write(combined, "png", file);

			if (ok) {
				return file;
			} else {
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 파일 고유명 반환
	 *
	 * @return
	 */
	public static String getUuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * DRM 암/복호화 여부 판단
	 *
	 * @param filename
	 * @return
	 */
	public static boolean isNonDRM(String filename){
		/*
		 * 2018-07-10 이미지파일과 PDF파일도 암호화 걸리도록 처리
		String ext[] = {
				".PNG"
				, ".JPG"
				, ".JPEG"
				, ".GIF"
				, ".TIF"
				, ".BMP"
				, ".PDF"
		};

		for(int i=0; i<ext.length; i++){
			if( filename.toUpperCase().indexOf(ext[i]) > 0 )
				return true;
		}
		*/
		return false;
	}

	public static byte[] imageToByteArray(File file) throws Exception {
		byte[] returnValue = null;

		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;

		try{
			baos = new ByteArrayOutputStream();
			fis = new FileInputStream(file);

			byte[] buf = new byte[1024];
			int read = 0;

			while((read=fis.read(buf, 0, buf.length)) != -1 ){
				baos.write(buf, 0, read);
			}

			returnValue = baos.toByteArray();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if( baos != null ){ baos.close(); }
			if( fis != null ) { fis.close(); }
		}

		return returnValue;
	}

	// 일단 IP 대역으로만 체크하고, 나중에 암호화 처리
	public static boolean checkEasyBAM(String com){
		boolean isLicenseOk = false;
		String ipAddress 	= null;
		String ipCheck		= null;
		try{
			ipAddress 	= InetAddress.getLocalHost().getHostAddress();
			ipCheck		= ipAddress.substring(0, ipAddress.lastIndexOf("."));

			if( "202.31.2".equals(ipCheck)
					|| "202.31.4".equals(ipCheck)
					|| "202.31.9".equals(ipCheck)
					|| "202.31.10".equals(ipCheck)
					|| "202.31.17".equals(ipCheck)
					|| "10.82.20".equals(ipCheck)
					|| "10.82.21".equals(ipCheck)
					|| "10.255.3.61".equals(ipCheck)
					|| "10.255.3.57".equals(ipCheck)
					){
				isLicenseOk = true;
			}
			isLicenseOk = true;
		}catch(Exception ex){

		}

		return isLicenseOk;
	}

	public static void copyFile(File file, File mfile) throws IOException{
		InputStream inStream = null;
		OutputStream outStream = null;

		try{
			inStream = new FileInputStream(file); //원본파일
			outStream = new FileOutputStream(mfile); //이동시킬 위치

			byte[] buffer = new byte[1024];
			int length;

			while ((length = inStream.read(buffer)) > 0){
				outStream.write(buffer, 0, length);
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			inStream.close();
			outStream.close();
		}
	}
}