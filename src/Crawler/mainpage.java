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

	JButton crawler = new JButton("��ȡ������ַ");
	JButton crawlers = new JButton("��ȡ�����ַ");
	JButton words = new JButton("�������дʿ�");
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
	JButton jButton = new JButton("��ȡ�������д�");

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
		urlstring = JOptionPane.showInputDialog("��������ַ��");
		try {
			URL url = new URL(urlstring);
			String source = getURLresource(url);
			jTextArea.append(source);
			

			String text = StripHT(source);
			jTextArea2.append(text);
	

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "��ַ��ʽ����", "��ʾ", JOptionPane.PLAIN_MESSAGE);
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
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();//��������
		int responseCode = httpURLConnection.getResponseCode();//��ȡӦ��
		StringBuffer sb = new StringBuffer("");
		try {
			if (responseCode == httpURLConnection.HTTP_OK) {//������ӳɹ�
				InputStream inputStream = httpURLConnection.getInputStream();//������
				BufferedReader br = new BufferedReader
						(new InputStreamReader(url.openStream(), "utf-8"));//��ȡ������
				String s = "";
				while ((s = br.readLine()) != null) {
					i++;
					sb.append(s + "\r\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();//������ȡ����
	}

	public static String StripHT(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // ����script��������ʽ
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // ����style��������ʽ
		String regEx_html = "<[^>]+>"; // ����HTML��ǩ��������ʽ

		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // ����script��ǩ

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // ����style��ǩ

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // ����html��ǩ
		htmlStr = htmlStr.trim();
		htmlStr = htmlStr.replaceAll(" ", "");
		htmlStr = htmlStr.replaceAll("\n", "");
		htmlStr = htmlStr.replaceAll("&nbsp", "");
		return htmlStr;
	}

	public static void filter(JTextArea j) throws Exception {
		String tex = j.getText();//��ȡ��ȡ����HTMLԴ��
		String regEx = "[`~!@#$%^&*()+=|{}:;\\[\\].<>/?~��@#��%����&*��������+|{}������������������������a-zA-Z 0-9]";
		//��������˵����ݵ�������ʽ
		Matcher m_data = ConverCompile(tex.toString().trim(), regEx);//����ƥ��ģʽ
		String result = m_data.replaceAll("").trim();//����

		String Phrase_Txt = readFile("���дʿ�.txt");//��ȡ���дʿ�
		String Rst = "";
		String[] s = Phrase_Txt.split("#");//�Դ�Ϊ��λ�ָ����дʿ����ַ���
	
		jTextArea2.setText("");//�����ʾ����
		jTextArea2.setText(result);//��ʾ���˱�ǩ����ı�
		Highlighter highlighter = jTextArea2.getHighlighter();
		DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(
				Color.yellow);//���ø���
		for (int i = 0; i < s.length; i++) {
			String reg = s[i];//������д�ɨ��
			int pos = 0;//λ��
			while ((pos = result.indexOf(reg, pos)) >= 0) {
				try {
					highlighter.addHighlight(pos, pos + reg.length(), painter);//����
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

	JButton select = new JButton("ѡ���ļ�������ȡ");
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
					public boolean accept(File f) { // �趨���õ��ļ��ĺ�׺��
						if (f.getName().endsWith(".txt") || f.isDirectory()) {
							return true;
						}
						return false;
					}

					public String getDescription() {
						return "txt�ļ�(*.txt)";
					}
				});

				jfc.showDialog(new JLabel(), "ѡ��");
				try {
					File file = jfc.getSelectedFile();

					if (file.isDirectory()) {
						System.out.println("�ļ���:" + file.getAbsolutePath());
					} else if (file.isFile()) {
						System.out.println("�ļ�:" + file.getAbsolutePath());
					}
					System.out.println(jfc.getSelectedFile().getName());
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					for (String line = br.readLine(); line != null; line = br.readLine()) {
						System.out.println(line);
						vStrings.add(line);
					}
					br.close();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(self, "��������������ѡ��", "��ʾ", JOptionPane.PLAIN_MESSAGE);
					return;
					// TODO: handle exception
				}
				
				for (String s : vStrings) {
					try {
						URL url = new URL(s);
						String source = crawler.getURLresource(url);
						String text = crawler.StripHT(source);
						String regEx = "[`~!@#$%^&*()+=|{}:;\\[\\].<>/?~��@#��%����&*��������+|{}������������������������a-zA-Z 0-9]";
						Matcher m_data = crawler.ConverCompile(text.trim(), regEx);
						String result = m_data.replaceAll("").trim();
						String Phrase_Txt = crawler.readFile("���дʿ�.txt");
						String Rst = "";
						String[] s1 = Phrase_Txt.split("#");

						File output = new File("���.txt");
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
				JOptionPane.showMessageDialog(self, "��ȡ����,����ȡ"+vStrings.size()+"��URL", "��ʾ", JOptionPane.PLAIN_MESSAGE);

			}

		});

	}
}

class words extends JFrame {
	JTextArea jTextArea = new JTextArea(15, 40);
	JButton revise = new JButton("����");
	JScrollPane jScrollPane = new JScrollPane(jTextArea);
	JLabel hint=new JLabel("�������дʲ��ԡ�#�����");

	public words() throws Exception {
		this.setTitle("���дʿ�");
		this.setSize(600, 650);
		this.setLocation(100, 100);
		this.setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.add(hint,BorderLayout.NORTH);
		this.add(jScrollPane, BorderLayout.CENTER);
		this.add(revise, BorderLayout.SOUTH);

		String Txt = crawler.readFile("���дʿ�.txt");
		jTextArea.setText(Txt.toString());

		revise.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String revise = jTextArea.getText();
				File file = new File("���дʿ�.txt");
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
