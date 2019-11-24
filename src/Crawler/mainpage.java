package Crawler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.*;
import java.nio.file.DirectoryStream.Filter;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.monitor.StringMonitorMBean;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.html.parser.Parser;

public class mainpage extends JFrame {

	JButton crawler = new JButton("爬取单个网址");
	JButton crawlers = new JButton("爬取多个网址");
	JButton words = new JButton("建立敏感词库");
	JPanel jPanel = new JPanel();

	public mainpage() throws Exception {
		this.setTitle("MAINPAGE");
		this.setSize(500, 200);
		this.setLocation(100, 100);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.add(jPanel);
		jPanel.add(crawler);
		jPanel.add(crawlers);
		jPanel.add(words);
		jPanel.setLayout(new GridLayout(1, 3));
		crawler.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					new crawler();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		crawlers.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e1) {
				try {
					new crawlers();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		words.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e2) {
				try {
					new words();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) throws Exception {
		new mainpage();
	}
}

class crawler extends JFrame {
	String urlstring = null;
	JPanel jPanel = new JPanel();
	JPanel jPanel2 = new JPanel();
	JPanel jPanel3 = new JPanel();
	JTextArea jTextArea = new JTextArea(15, 48);
	static JTextArea jTextArea2 = new JTextArea(2, 48);
	JScrollPane jScrollPane = new JScrollPane(jTextArea);
	JScrollPane jScrollPane2 = new JScrollPane(jTextArea2);
	JButton jButton = new JButton("提取高亮敏感词");

	public crawler() throws Exception {
		this.setTitle("URL");
		this.setSize(600, 650);
		this.setLocation(100, 100);
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.add(jPanel3);
		jPanel3.add(jPanel, BorderLayout.NORTH);
		jPanel3.add(jPanel2, BorderLayout.SOUTH);
		jPanel.setLayout(new GridLayout(2, 1));
		jPanel.add(jScrollPane);
		jPanel.add(jScrollPane2);
		jTextArea.setEditable(false);
		jTextArea.setLineWrap(true);
		jTextArea2.setEditable(false);
		jTextArea2.setLineWrap(true);
		jTextArea2.setText("");
		jPanel2.add(jButton);
		urlstring = JOptionPane.showInputDialog("请输入网址：");
		try {
			URL url = new URL(urlstring);
			String source = getURLresource(url);
			jTextArea.append(source);
			

			String text = StripHT(source);
			jTextArea2.append(text);
	

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "网址格式有误", "提示", JOptionPane.PLAIN_MESSAGE);
			dispose();
		}

		jButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					filter(jTextArea2);
				
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public static String getURLresource(URL url) throws Exception {
		int i = 0;
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();//建立连接
		int responseCode = httpURLConnection.getResponseCode();//获取应答
		StringBuffer sb = new StringBuffer("");
		try {
			if (responseCode == httpURLConnection.HTTP_OK) {//如果连接成功
				InputStream inputStream = httpURLConnection.getInputStream();//输入流
				BufferedReader br = new BufferedReader
						(new InputStreamReader(url.openStream(), "utf-8"));//读取输入流
				String s = "";
				while ((s = br.readLine()) != null) {
					i++;
					sb.append(s + "\r\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();//返回爬取内容
	}

	public static String StripHT(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签
		htmlStr = htmlStr.trim();
		htmlStr = htmlStr.replaceAll(" ", "");
		htmlStr = htmlStr.replaceAll("\n", "");
		htmlStr = htmlStr.replaceAll("&nbsp", "");
		return htmlStr;
	}

	public static void filter(JTextArea j) throws Exception {
		String tex = j.getText();//获取爬取到的HTML源码
		String regEx = "[`~!@#$%^&*()+=|{}:;\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？a-zA-Z 0-9]";
		//设置需过滤的内容的正则表达式
		Matcher m_data = ConverCompile(tex.toString().trim(), regEx);//建立匹配模式
		String result = m_data.replaceAll("").trim();//过滤

		String Phrase_Txt = readFile("敏感词库.txt");//读取敏感词库
		String Rst = "";
		String[] s = Phrase_Txt.split("#");//以词为单位分割敏感词库中字符串
	
		jTextArea2.setText("");//清空显示区域
		jTextArea2.setText(result);//显示过滤标签后的文本
		Highlighter highlighter = jTextArea2.getHighlighter();
		DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(
				Color.yellow);//设置高亮
		for (int i = 0; i < s.length; i++) {
			String reg = s[i];//逐个敏感词扫描
			int pos = 0;//位置
			while ((pos = result.indexOf(reg, pos)) >= 0) {
				try {
					highlighter.addHighlight(pos, pos + reg.length(), painter);//高亮
					pos += reg.length();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		

	}

	public static Matcher ConverCompile(String result, String regEx) {
		Pattern c = Pattern.compile(regEx);
		Matcher mc = c.matcher(result);
		return mc;
	}

	public static String readFile(String road) {
		File file = new File(road);
		String encoding = "GBK";
		String lineTXT = null;
		if (file.isFile() && file.exists()) {
			try {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				lineTXT = bufferedReader.readLine();
				read.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return lineTXT;
	}

}

class crawlers extends JFrame {

	JButton select = new JButton("选择文件批量爬取");
	Vector<String> vStrings = new Vector<>();

	public crawlers() throws Exception {
		crawlers self = this;
		this.setTitle("URLs");
		this.setSize(200, 200);
		this.setLocation(200, 200);
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.add(select, BorderLayout.CENTER);

		select.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
					public boolean accept(File f) { // 设定可用的文件的后缀名
						if (f.getName().endsWith(".txt") || f.isDirectory()) {
							return true;
						}
						return false;
					}

					public String getDescription() {
						return "txt文件(*.txt)";
					}
				});

				jfc.showDialog(new JLabel(), "选择");
				try {
					File file = jfc.getSelectedFile();

					if (file.isDirectory()) {
						System.out.println("文件夹:" + file.getAbsolutePath());
					} else if (file.isFile()) {
						System.out.println("文件:" + file.getAbsolutePath());
					}
					System.out.println(jfc.getSelectedFile().getName());
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					for (String line = br.readLine(); line != null; line = br.readLine()) {
						System.out.println(line);
						vStrings.add(line);
					}
					br.close();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(self, "操作有误，请重新选择", "提示", JOptionPane.PLAIN_MESSAGE);
					return;
					// TODO: handle exception
				}
				
				for (String s : vStrings) {
					try {
						URL url = new URL(s);
						String source = crawler.getURLresource(url);
						String text = crawler.StripHT(source);
						String regEx = "[`~!@#$%^&*()+=|{}:;\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？a-zA-Z 0-9]";
						Matcher m_data = crawler.ConverCompile(text.trim(), regEx);
						String result = m_data.replaceAll("").trim();
						String Phrase_Txt = crawler.readFile("敏感词库.txt");
						String Rst = "";
						String[] s1 = Phrase_Txt.split("#");

						File output = new File("结果.txt");
						FileWriter fileWriter = new FileWriter(output, true);
						PrintWriter pWriter = new PrintWriter(fileWriter);
						pWriter.println(s);
						pWriter.flush();
						for (int i = 0; i < s1.length; i++) {
							String reg = s1[i];
							int times = 0;
							int pos = 0;
							while ((pos = result.indexOf(reg, pos)) >= 0) {
								times++;
								pos += reg.length();
							}
							if (times> 0) {
								try {
									pWriter.println(reg+"  "+times);
									pWriter.flush();
									fileWriter.flush();
									pos += reg.length();
									System.out.println(reg);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						
						pWriter.close();
						fileWriter.close();

					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				JOptionPane.showMessageDialog(self, "爬取结束,共爬取"+vStrings.size()+"个URL", "提示", JOptionPane.PLAIN_MESSAGE);

			}

		});

	}
}

class words extends JFrame {
	JTextArea jTextArea = new JTextArea(15, 40);
	JButton revise = new JButton("保存");
	JScrollPane jScrollPane = new JScrollPane(jTextArea);
	JLabel hint=new JLabel("输入敏感词并以“#”间隔");

	public words() throws Exception {
		this.setTitle("敏感词库");
		this.setSize(600, 650);
		this.setLocation(100, 100);
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.add(hint,BorderLayout.NORTH);
		this.add(jScrollPane, BorderLayout.CENTER);
		this.add(revise, BorderLayout.SOUTH);

		String Txt = crawler.readFile("敏感词库.txt");
		jTextArea.setText(Txt.toString());

		revise.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String revise = jTextArea.getText();
				File file = new File("敏感词库.txt");
				try {
					PrintStream pStream = new PrintStream(file);
					pStream.println(revise);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

	}

}
