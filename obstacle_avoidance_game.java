
// 모바일소프트웨어/웹공학 2071465 홍우창
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.text.DecimalFormat;

public class Oop2Hw2 extends JFrame {
	private final int FLYING_UNIT = 12; // 캐릭터와 장애물의 이동 단위
	private JLabel la = new JLabel("□"); // 플레이어 캐릭터
	private RandomThread th; // 스레드 레퍼런스

	public Oop2Hw2() {
		setTitle("상,하,좌,우키를 이용하여 장애물 피하기");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container c = getContentPane();
		c.setLayout(null);
		c.addKeyListener(new MyKeyListener());

		// 캐릭터 초기 위치 및 크기 설정
		la.setLocation(144, 108);
		la.setSize(15, 15);
		c.add(la);

		setSize(302, 304); // 윈도우 크기 설정
		setVisible(true);

		c.setFocusable(true);
		c.requestFocus();

		th = new RandomThread(c); // 스레드 생성
		th.start(); // 스레드 동작시킴
	}

	// 키 이벤트 처리 클래스
	class MyKeyListener extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_UP:
				// 캐릭터가 윈도우 상단을 넘어가지 않도록
				if ((la.getY() - FLYING_UNIT) >= 0) {
					la.setLocation(la.getX(), la.getY() - FLYING_UNIT);
				}
				break;
			case KeyEvent.VK_DOWN:
				// 캐릭터가 윈도우 하단을 넘어가지 않도록
				if ((la.getY() + FLYING_UNIT) < getContentPane().getHeight() - 4) {
					la.setLocation(la.getX(), la.getY() + FLYING_UNIT);
				}
				break;
			case KeyEvent.VK_LEFT:
				// 캐릭터가 윈도우 왼쪽을 넘어가지 않도록
				if ((la.getX() - FLYING_UNIT) >= 0) {
					la.setLocation(la.getX() - FLYING_UNIT, la.getY());
				}
				break;
			case KeyEvent.VK_RIGHT:
				// 캐릭터가 윈도우 오른쪽을 넘어가지 않도록
				if ((la.getX() + FLYING_UNIT) < getContentPane().getWidth()) {
					la.setLocation(la.getX() + FLYING_UNIT, la.getY());
				}
				break;
			}
		}
	}

	// 장애물 생성 및 이동을 처리하는 스레드 클래스
	class RandomThread extends Thread {
		private Container contentPane;
		private boolean failed_flag = false; // 게임 오버 플래그 true: 종료 지시
		private boolean success_failed_flag = false;

		private JLabel[] obstacles = new JLabel[3000]; // 장애물 배열 (게임 도중 총 3000개의 장애물이 나온다)
		private int obstacleCount = 0; // 현재 화면에 표시된 장애물의 개수
		private int lastObstacleAddedTime = -5; // 마지막으로 장애물이 추가된 시점
		private int sleepTime = 500; // 스레드 sleep 시간
		private int timecheck = 5; // 장애물 생성 주기

		public RandomThread(Container contentPane) {
			this.contentPane = contentPane;

			// 장애물 초기 설정
			for (int i = 0; i < obstacles.length; i++) {
				int y = ((int) (Math.random() * 22)); // 0~22까지의 난수 생성
				JLabel obstacle = new JLabel("■"); // 장애물 그림
				obstacle.setSize(15, 15);

				if (i % 2 == 0) { // 짝수번째의 장애물이라면
					obstacle.setLocation(-12, y * FLYING_UNIT); // 왼쪽 벽에서 생성
				} else { // 그렇지 않고 홀수번째의 장애물이라면
					obstacle.setLocation(300 - FLYING_UNIT, y * FLYING_UNIT); // 오른쪽에서 생성
					obstacle.setForeground(Color.RED); // 그리고 오른쪽에서 나오는 장애물을 빨간색으로 설정. 알아보기 쉽게
				}
				obstacles[i] = obstacle; // 장애물 설정 저장
			}
		}

		// 게임 오버 처리
		void finish() {
			failed_flag = true;
		}

		// 게임 성공 처리
		void success() {
			success_failed_flag = true;
		}

		@Override
		public void run() {
			long startTime = System.currentTimeMillis(); // 시작시간 저장
			int obstacleAddCount = 2; // 한 번에 추가되는 장애물의 개수를 저장하는 변수

			while (true) {
				long currentTime = System.currentTimeMillis(); // 현재시간 변수
				int workingtime = (int) ((currentTime - startTime) / 1000); // 게임을 시작한 후의 시간 변수

				// 장애물 생성 주기에 따른 장애물 생성
				if (workingtime - lastObstacleAddedTime >= timecheck) {
					// 장애물이 배열 범위 내에 있다면
					if (obstacleCount + obstacleAddCount <= obstacles.length) {
						// obstacleAddCount 만큼 장애물 추가
						for (int i = 0; i < obstacleAddCount; i++) {
							contentPane.add(obstacles[obstacleCount++]); // obstacleCount의 번호수의 배열의 장애물 생성
							// 초반에 3000개 범위를 주었기 때문에 3000개까지의 장애물이 생성 가능한 범위이다.
						}
					}
					if (sleepTime >= 100) { // 만약 장애물 이동속도가 0.1초 이상이라면
						sleepTime -= 50; // sleep 시간을 0.05초 줄임. (난이도 증가)
					}
					if (timecheck > 1) { // 만약 장애물 생성 주기가 1초 이상라면
						timecheck -= 0.25; // 주기 0.25초 감소 (난이도 증가)
					}
					lastObstacleAddedTime = workingtime; // 장애물 추가 시점 업데이트.
					if (obstacleAddCount < 10) { // 장애물의 수가 16개보다 적다면
						obstacleAddCount += 2; // 한 번에 추가되는 장애물의 개수를 2개씩 늘림
					}
					if (obstacleCount >= 3000) { // 만약 장애물을 3000개까지 생성을 했다면
						success(); // 게임 성공
					}

					// 게임 성공 처리
					if (success_failed_flag == true) {
						contentPane.removeAll();
						JLabel label = new JLabel("SUCCESS!");
						label.setSize(80, 30);
						label.setLocation(113, 100);
						label.setForeground(Color.RED);
						contentPane.add(label);

						// 살아남은 시간 표시
						long endTime = System.currentTimeMillis(); // 끝나는 시점 저장
						double survivedTime = (endTime - startTime) / 1000.0; // 플레이한 시간 변수
						DecimalFormat df = new DecimalFormat("0.00"); // 소수점 둘째자리
						JLabel timeLabel = new JLabel("Survived Time: " + df.format(survivedTime) + " seconds");
						timeLabel.setSize(200, 30);
						timeLabel.setLocation(65, 130);
						contentPane.add(timeLabel);
						contentPane.repaint();
						return;
					}
				}

				// 장애물 이동 처리
				try {
					for (int i = 0; i < obstacleCount; i++) {
						if (failed_flag) { // 게임 오버라면 for문 중단 후 게임오버처리 if문으로
							break;
						}
						JLabel obstacle = obstacles[i];
						checkCollision(obstacle); // 장애물이 이동 '전' 에 캐릭터와 겹쳤는지 검사하는 함수
						// 이동 전에 검사를 하지 않으면 멈춰있는 장애물에 닿았다가 다시 돌아와도 게임이 종료되지 않음
						obstacle.setLocation(obstacle.getX() + (i % 2 == 0 ? FLYING_UNIT : -FLYING_UNIT),
								obstacle.getY());
						checkCollision(obstacle); // 장애물이 이동 '후' 에 캐릭터와 겹쳤는지 검사하는 함수

					}
					Thread.sleep(sleepTime); // sleepTime 만큼 멈춤 (장애물 이동 시간 주기)

					// 게임 오버 처리
					if (failed_flag == true) {
						contentPane.removeAll();
						JLabel label = new JLabel("FAILED!");
						label.setSize(80, 30);
						label.setLocation(120, 100);
						label.setForeground(Color.RED);
						contentPane.add(label);

						// 살아남은 시간 표시
						long endTime = System.currentTimeMillis(); // 끝나는 시점 저장
						double survivedTime = (endTime - startTime) / 1000.0; // 플레이한 시간 변수
						DecimalFormat df = new DecimalFormat("0.00"); // 소수점 둘째자리
						JLabel timeLabel = new JLabel("Survived Time: " + df.format(survivedTime) + " seconds");
						timeLabel.setSize(200, 30);
						timeLabel.setLocation(65, 130);
						contentPane.add(timeLabel);
						contentPane.repaint();
						return;
					}
				} catch (InterruptedException e) {
					return;
				}
			}
		}

		// 캐릭터와 장애물의 충돌 체크
		private void checkCollision(JLabel obstacle) {
			int characterX = la.getX(); // 내 캐릭터의 x좌표와 y좌표
			int characterY = la.getY();
			int obstacleX = obstacle.getX(); // 장애물의 x좌표와 y좌표
			int obstacleY = obstacle.getY();
			if (characterX == obstacleX && characterY == obstacleY) { // 만약 좌표가 겹친다면
				finish(); // 게임 오버
			}
		}
	}

	public static void main(String[] args) {
		new Oop2Hw2();
	}
}
