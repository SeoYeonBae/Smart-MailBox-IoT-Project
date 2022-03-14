import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ColorConvertOp;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class MailBox extends JFrame{
	final static int POST = 1;
	final static int DELIVERY = 2;

	final String FrameTitle = "packages";
	final String COVER = "/root/Desktop/proj/src/res/cover.png";
	final String POSTBTN = "/root/Desktop/proj/src/res/postBtn.png";
	final String DELBTN = "/root/Desktop/proj/src/res/delBtn.png";
	final String LIGHTAUTOBTN = "/root/Desktop/proj/src/res/lightAutoBtn.png";
	final String LIGHTAUTOBTNON = "/root/Desktop/proj/src/res/lightAutoBtnOn.png";
	final String LIGHTMANBTN = "/root/Desktop/proj/src/res/lightManBtn.png";
	final String LIGHTMANBTNON = "/root/Desktop/proj/src/res/lightManBtnOn.png";
	final String LIGHTBTN = "/root/Desktop/proj/src/res/lightBtn.png";
	final String LOCKBTN = "/root/Desktop/proj/src/res/lockBtn.png";
	final String UNLOCKBTN = "/root/Desktop/proj/src/res/unlockBtn.png";
	final String BACKGROUND = "/root/Desktop/proj/src/res/background.png";
	final String POSTIMG = "/root/Desktop/proj/src/res/postbackground.png";
	final String DELIMG = "/root/Desktop/proj/src/res/delbackground.png";
	final String ZERO = "/root/Desktop/proj/src/res/0.png";
	final String ONE = "/root/Desktop/proj/src/res/1.png";
	final String TWO = "/root/Desktop/proj/src/res/2.png";
	final String THREE = "/root/Desktop/proj/src/res/3.png";
	final String FOUR = "/root/Desktop/proj/src/res/4.png";
	final String FIVE = "/root/Desktop/proj/src/res/5.png";
	final String SIX = "/root/Desktop/proj/src/res/6.png";
	final String SEVEN = "/root/Desktop/proj/src/res/7.png";
	final String EIGHT = "/root/Desktop/proj/src/res/8.png";
	final String NINE = "/root/Desktop/proj/src/res/9.png";
	final String OBACK = "/root/Desktop/proj/src/res/oback.png";
	final String CLC = "/root/Desktop/proj/src/res/clcBtn.png";
	final String OK = "/root/Desktop/proj/src/res/okBtn.png";
	final String MAINBACK = "/root/Desktop/proj/src/res/backBtn.png";
	final String NEW = "/root/Desktop/proj/src/res/new.png";
	final String EXIT = "/root/Desktop/proj/src/res/exit.png";

	final int WIN_WIDTH = 507; // 전체 frame의 폭
	final int WIN_HEIGHT = 796; // 전체 frame의 높이
	
	int packages;
	String revPostPw;
	String revDelPw;

	boolean postAutoOn = false;
	boolean delAutoOn = false;
	boolean postManOn = false;
	boolean delManOn = false;
	boolean postArrive = false;
	boolean delArrive = false;

	CardLayout card;

	JPanel coverPanel;
	JPanel packagesPanel;
	JPanel pwPanel;

	JButton postBtn;
	JButton deliveryBtn;
	JButton lightAutoBtn;
	JButton lightManBtn;
	JButton lockBtn;
	JButton unlockBtn;
	JButton btn0;
	JButton btn1;
	JButton btn2;
	JButton btn3;
	JButton btn4;
	JButton btn5;
	JButton btn6;
	JButton btn7;
	JButton btn8;
	JButton btn9;
	JButton oBack;
	JButton clcBtn;
	JButton okBtn;
	JButton mainBckBtn;
	JButton exitBtn;

	JTextField pw;
	JTextField postPw;
	JTextField delPw;
	
	ImageIcon coverImage;
	ImageIcon backImage;
	ImageIcon postImage;
	ImageIcon delImage;
	ImageIcon newPacImage;

	static ServerSocket serversock;
	static Socket socket;
	static BufferedReader in;
	static PrintWriter out;
	
	public static void main(String[] args) {
			MailBox mb = new MailBox();
	}

	MailBox(){
		setTitle(FrameTitle);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIN_WIDTH, WIN_HEIGHT);
		setLocationRelativeTo(null); // 프레임 중앙에 띄우기
		setResizable(false); // 프레임 사이즈 고정

		coverImage = new ImageIcon(COVER);
		backImage = new ImageIcon(BACKGROUND);
		postImage = new ImageIcon(POSTIMG);
		delImage = new ImageIcon(DELIMG);
		newPacImage = new ImageIcon(NEW);

		card = new CardLayout();
		getContentPane().setLayout(card); // 레이아웃을 카드로 설정하고 프레임에 씌우기

		coverPanel = new CoverPanel(); // 패널 만들기
		packagesPanel = new PackagesPanel();
		pwPanel = new PWPanel();

		postBtn = new JButton(new ImageIcon(POSTBTN));
		deliveryBtn = new JButton(new ImageIcon(DELBTN));
		lightAutoBtn = new JButton(new ImageIcon(LIGHTAUTOBTN));
		lightManBtn = new JButton(new ImageIcon(LIGHTMANBTN));
		lockBtn = new JButton(new ImageIcon(LOCKBTN));
		unlockBtn = new JButton(new ImageIcon(UNLOCKBTN));
		btn0 = new JButton(new ImageIcon(ZERO));
		btn1 = new JButton(new ImageIcon(ONE));
		btn2 = new JButton(new ImageIcon(TWO));
		btn3 = new JButton(new ImageIcon(THREE));
		btn4 = new JButton(new ImageIcon(FOUR));
		btn5 = new JButton(new ImageIcon(FIVE));
		btn6 = new JButton(new ImageIcon(SIX));
		btn7 = new JButton(new ImageIcon(SEVEN));
		btn8 = new JButton(new ImageIcon(EIGHT));
		btn9 = new JButton(new ImageIcon(NINE));
		oBack = new JButton(new ImageIcon(OBACK));
		clcBtn = new JButton(new ImageIcon(CLC));
		okBtn = new JButton(new ImageIcon(OK));
		mainBckBtn = new JButton(new ImageIcon(MAINBACK));
		exitBtn = new JButton(new ImageIcon(EXIT));

		pw = new JTextField(1);
		pw.setHorizontalAlignment(JTextField.CENTER);
		pw.setBorder(javax.swing.BorderFactory.createEmptyBorder());		
		pw.setFont(new Font("한컴 백제 B",Font.PLAIN,70));	
		postPw = new JTextField(1);
		postPw.setHorizontalAlignment(JTextField.CENTER);
		postPw.setBorder(javax.swing.BorderFactory.createEmptyBorder());		
		postPw.setFont(new Font("한컴 백제 B",Font.PLAIN,30));	
		postPw.setBackground(new Color(119,224,249));
		delPw = new JTextField(1);
		delPw.setHorizontalAlignment(JTextField.CENTER);
		delPw.setBorder(javax.swing.BorderFactory.createEmptyBorder());		
		delPw.setFont(new Font("한컴 백제 B",Font.PLAIN,30));	
		delPw.setBackground(new Color(119,224,249));

		postBtn.setContentAreaFilled(false);
		deliveryBtn.setContentAreaFilled(false);
		lightAutoBtn.setContentAreaFilled(false);
		lightAutoBtn.setBorderPainted(false);
		lightManBtn.setContentAreaFilled(false);
		lightManBtn.setBorderPainted(false);
		lockBtn.setContentAreaFilled(false);
		lockBtn.setBorderPainted(false);
		unlockBtn.setContentAreaFilled(false);
		unlockBtn.setBorderPainted(false);
		btn0.setContentAreaFilled(false);
		btn1.setContentAreaFilled(false);
		btn2.setContentAreaFilled(false);
		btn3.setContentAreaFilled(false);
		btn4.setContentAreaFilled(false);
		btn5.setContentAreaFilled(false);
		btn6.setContentAreaFilled(false);
		btn7.setContentAreaFilled(false);
		btn8.setContentAreaFilled(false);
		btn9.setContentAreaFilled(false);
		oBack.setContentAreaFilled(false);
		okBtn.setContentAreaFilled(false);
		clcBtn.setContentAreaFilled(false);
		mainBckBtn.setContentAreaFilled(false);
		exitBtn.setContentAreaFilled(false);
		btn0.setBorderPainted(false);
		btn1.setBorderPainted(false);
		btn2.setBorderPainted(false);
		btn3.setBorderPainted(false);
		btn4.setBorderPainted(false);
		btn5.setBorderPainted(false);
		btn6.setBorderPainted(false);
		btn7.setBorderPainted(false);
		btn8.setBorderPainted(false);
		btn9.setBorderPainted(false);
		oBack.setBorderPainted(false);
		okBtn.setBorderPainted(false);
		clcBtn.setBorderPainted(false);
		exitBtn.setBorderPainted(false);
		mainBckBtn.setBorderPainted(false);
		
		postBtn.addActionListener(new BtnListener());
		deliveryBtn.addActionListener(new BtnListener());
		lightAutoBtn.addActionListener(new BtnListener());
		lightManBtn.addActionListener(new BtnListener());
		lockBtn.addActionListener(new BtnListener());
		unlockBtn.addActionListener(new BtnListener());
		btn0.addActionListener(new NumBtnListener());
		btn1.addActionListener(new NumBtnListener());
		btn2.addActionListener(new NumBtnListener());
		btn3.addActionListener(new NumBtnListener());
		btn4.addActionListener(new NumBtnListener());
		btn5.addActionListener(new NumBtnListener());
		btn6.addActionListener(new NumBtnListener());
		btn7.addActionListener(new NumBtnListener());
		btn8.addActionListener(new NumBtnListener());
		btn9.addActionListener(new NumBtnListener());
		oBack.addActionListener(new BtnListener());
		okBtn.addActionListener(new NumBtnListener());
		clcBtn.addActionListener(new NumBtnListener());
		mainBckBtn.addActionListener(new BtnListener());
		exitBtn.addActionListener(new BtnListener());

		coverPanel.add(postBtn);
		coverPanel.add(deliveryBtn);
		coverPanel.add(exitBtn);
		packagesPanel.add(lightAutoBtn);
		packagesPanel.add(lightManBtn);
		packagesPanel.add(lockBtn);
		packagesPanel.add(unlockBtn);
		packagesPanel.add(mainBckBtn);
		packagesPanel.add(postPw);
		packagesPanel.add(delPw);
		pwPanel.add(btn0);
		pwPanel.add(btn1);
		pwPanel.add(btn2);
		pwPanel.add(btn3);
		pwPanel.add(btn4);
		pwPanel.add(btn5);
		pwPanel.add(btn6);
		pwPanel.add(btn7);
		pwPanel.add(btn8);
		pwPanel.add(btn9);
		pwPanel.add(oBack);
		pwPanel.add(okBtn);
		pwPanel.add(clcBtn);
		pwPanel.add(pw);
		
		String rev;
		getContentPane().add("cover", coverPanel); // 카드레이아웃에 "coverPanel"이란 이름의 coverPanel을 추가한다
		getContentPane().add("packages",packagesPanel);
		getContentPane().add("pw",pwPanel);
		card.show(getContentPane(), "cover");

		requestFocus();
		setFocusable(true);
		setVisible(true);

		try{
			serversock = new ServerSocket(8765);
			System.out.println("클라이언트 접속 대기 중...");
			socket = serversock.accept();
			System.out.println("클라이언트 접속");
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			while (true){
				rev = in.readLine();
				if(!(rev.isEmpty())){
					if(rev.equals("post_y")){
						postArrive = true;
						coverPanel.repaint();
					}
					else if(rev.equals("post_n")){
						postArrive = false;
						coverPanel.repaint();
					}					
					else if(rev.equals("del_y")){
						delArrive = true;
						coverPanel.repaint();
					}
					else if(rev.equals("del_n")){
						delArrive = false;
						coverPanel.repaint();
					}
					else if(rev.equals("Goodbye~~")){
						try{				
							System.out.println("접속 종료");
							socket.close();
							serversock.close();
							System.exit(0);
						}catch(Exception ex){}
					}else{
						if (postArrive){
							revPostPw = rev;
							postPw.setText(revPostPw);
							packagesPanel.repaint();
						}
						else if(delArrive){
							revDelPw = rev;
							delPw.setText(revDelPw);
							packagesPanel.repaint();
						}
					}
				}else{}
			}
		}catch(Exception e){
		}
	}
	class CoverPanel extends JPanel {
		public void paintComponent(Graphics g) {
			g.drawImage(coverImage.getImage(), 0, 0, null); // 이미지 그리기
			postBtn.setBounds(87, 505, 337, 74);
			deliveryBtn.setBounds(87, 610, 337, 74);
			exitBtn.setBounds(445,10,50,60);
			if(postArrive)
				g.drawImage(newPacImage.getImage(), 360, 440, 131, 131, null);
			if(delArrive)
				g.drawImage(newPacImage.getImage(), 360, 545, 131, 131, null);
		}
	}// CoverPanel class end
	class PackagesPanel extends JPanel{
		public void paintComponent(Graphics g) {
			g.drawImage(backImage.getImage(), 0, 0, null);
			if(packages == POST) {
				g.drawImage(postImage.getImage(), 137, 121, 239, 333, null);
				postPw.setBounds(304, 550, 80, 40);
				delPw.setBounds(0, 0, 0, 0);
			}
			else {
				g.drawImage(delImage.getImage(), 96, 121, 321, 303, null);
				delPw.setBounds(304, 550, 80, 40);
				postPw.setBounds(0, 0, 0, 0);
			}
			lightAutoBtn.setBounds(30, 245, 140, 213);
			lightManBtn.setBounds(340, 245, 140, 213);
			lockBtn.setBounds(130, 445, 84, 121);
			unlockBtn.setBounds(300, 445, 84, 121);
			mainBckBtn.setBounds(420, 15, 75, 57);
		}
	}
	class PWPanel extends JPanel{
		public void paintComponent(Graphics g) {
			g.drawImage(backImage.getImage(), 0, 0, null);
			oBack.setBounds(420, 15, 58,58);
			btn1.setBounds(70, 240, 100, 100);
			btn2.setBounds(200, 240, 100, 100);
			btn3.setBounds(330, 240, 100, 100);
			btn4.setBounds(70, 370, 100, 100);
			btn5.setBounds(200, 370, 100, 100);
			btn6.setBounds(330, 370, 100, 100);
			btn7.setBounds(70, 500, 100, 100);
			btn8.setBounds(200, 500, 100, 100);
			btn9.setBounds(330, 500, 100, 100);
			clcBtn.setBounds(70, 630, 100, 100);
			btn0.setBounds(200, 630, 100, 100);
			okBtn.setBounds(330, 630, 100, 100);
			pw.setBounds(130,90,250,110);
		}
	}
	public class BtnListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == postBtn) {
				packages = POST; 
				card.show(getContentPane(), "packages");
			}
			else if(e.getSource() == deliveryBtn) {
				packages = DELIVERY;
				card.show(getContentPane(), "packages");
			}
			else if(e.getSource() == unlockBtn) {
				if(delPw.getText().isEmpty() && packages == DELIVERY){
					try{
						out.println("del_unlock");
					}catch(Exception ex){}
				}
				else if (postPw.getText().isEmpty() && packages == POST){
					try{
						out.println("post_unlock");
					}catch(Exception ex){}
				}
				else
					card.show(getContentPane(), "pw");
			}
			else if (e.getSource() == oBack) {
				card.show(getContentPane(), "packages");
				pw.setText("");
			}	
			else if(e.getSource()== mainBckBtn) {
				card.show(getContentPane(), "cover");
			}
			else if(e.getSource() == lightAutoBtn){
				if(packages == POST) {
					if(postAutoOn) {		// 자동기능이 켜져있을 때 누르면
						postAutoOn = false;
						lightAutoBtn.setIcon(new ImageIcon(LIGHTAUTOBTN));
					}
					else {						// 자동기능이 꺼져있을 때 누르면
						postAutoOn = true;
						lightAutoBtn.setIcon(new ImageIcon(LIGHTAUTOBTNON));
					}
					try{
							out.println("post_light_auto");
						}catch(Exception ex){}
				}
				else{
					if(delAutoOn) {
						delAutoOn = false;
						lightAutoBtn.setIcon(new ImageIcon(LIGHTAUTOBTN));
					}
					else {
						delAutoOn = true;
						lightAutoBtn.setIcon(new ImageIcon(LIGHTAUTOBTNON));
					}
					try{
							out.println("del_light_auto");
						}catch(Exception ex){}
				}
			}
			else if(e.getSource() == lightManBtn){
				if(packages == POST) {
					if(postManOn) {					// 불이 켜져 있을 때 누르면 
						postManOn = false;
						lightManBtn.setIcon(new ImageIcon(LIGHTMANBTN));
					}
					else {								// 불이 꺼져 있을 때 누르면 
						postManOn = true;
						lightManBtn.setIcon(new ImageIcon(LIGHTMANBTNON));
						postAutoOn = false;
						lightAutoBtn.setIcon(new ImageIcon(LIGHTAUTOBTN));
					}
					try{
						out.println("post_light_man");
					}catch(Exception ex){}
				}
				else {
					if(delManOn) {
						delManOn = false;
						lightManBtn.setIcon(new ImageIcon(LIGHTMANBTN));
					}
					else {
						delManOn = true;
						lightManBtn.setIcon(new ImageIcon(LIGHTMANBTNON));
						delAutoOn = false;
						lightAutoBtn.setIcon(new ImageIcon(LIGHTAUTOBTN));
					}
					try{
						out.println("del_light_man");
					}catch(Exception ex){}
				}
			}
			else if(e.getSource() == lockBtn){
				if(packages==POST){
					try{
							out.println("post_lock");
						}catch(Exception ex){}
				}
				else{
					try{
							out.println("del_lock");
						}catch(Exception ex){}
				}
			}
			else if (e.getSource() == exitBtn){
				try{		
					System.out.println("접속 종료");		
					out.println("exit");
					socket.close();
					serversock.close();
					System.exit(0);
				}catch(Exception ex){}

			}	
		}
		
	}
	public class NumBtnListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String text = pw.getText();
			if(e.getSource()==btn0) 
				pw.setText(text + "0");
			else if(e.getSource()==btn1) 
				pw.setText(text + "1");
			else if(e.getSource()==btn2) 
				pw.setText(text + "2");
			else if(e.getSource()==btn3) 
				pw.setText(text + "3");
			else if(e.getSource()==btn4) 
				pw.setText(text + "4");
			else if(e.getSource()==btn5) 
				pw.setText(text + "5");
			else if(e.getSource()==btn6) 
				pw.setText(text + "6");
			else if(e.getSource()==btn7) 
				pw.setText(text + "7");
			else if(e.getSource()==btn8) 
				pw.setText(text + "8");
			else if(e.getSource()==btn9) 
				pw.setText(text + "9");
			else if(e.getSource() == clcBtn)
				pw.setText("");
			else if(e.getSource() == okBtn){
				String inPw = pw.getText();
				if(packages == POST){
					if(inPw.equals(revPostPw)){
						pw.setText("");
						try{	
							out.println("m_correct");		
							out.println("post_unlock");
							postPw.setText("");
							card.show(getContentPane(), "cover");				
						}catch(Exception ex){}		
					}
					else{
						pw.setText("");
						try{
							out.println("m_wrong");
						}catch(Exception ex){}							
					}
				}
				else{
					if(inPw.equals(revDelPw)){
						pw.setText("");
						try{	
							out.println("m_correct");		
							out.println("del_unlock");
							delPw.setText("");
							card.show(getContentPane(), "cover");				
						}catch(Exception ex){}		
					}
					else{
						pw.setText("");
						try{
							out.println("m_wrong");
						}catch(Exception ex){}							
					}
				}			
			}
		}
	}
}

