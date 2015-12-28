package ru.tomsk.pear.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
//import android.view.View;

public class Game extends SurfaceView implements SurfaceHolder.Callback {
	
	TetrisCanvas tetris;
	TetrisThread _thread;
	
	public Game(Context context) {
		super(context);
		getHolder().addCallback(this);
		tetris = new TetrisCanvas();
		_thread = new TetrisThread(getHolder(), this);
		
	}
	
	/**
	 * Главный рисовальный метод
	 */
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//if (tetris.isGameOver()) tetris.start();
		drawInterface(canvas);
		paintTetris(canvas, _windowLeft, _windowTop, _offset);//, paint);
	}	
	
	/**
	 * Рисует полотно тетриса, фигуру и занятые поля
	 * @param canvas
	 * @param left левая граница, откуда нужно рисовать
	 * @param top верхняя граница, откуда нужно рисовать
	 * @param offset расстояние между "точками" фигур
	 */
	private void paintTetris(Canvas canvas, float left, float top, float offset/*, Paint pain_t*/) {
		float radius = (offset/2.3f);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLUE);
		byte[][] tetCanv = tetris.getCurCanvas();

		for (int i = 0; i < tetCanv.length; i++) {
			for (int j = 0; j < tetCanv[i].length; j++) {
				if (tetCanv[i][j] == 1) {
					//canvas.drawCircle(left + offset*j + offset/2, top + offset*i + offset/2, radius, paint);
					canvas.drawRect(left + offset*j, top + offset*i, left + offset*j + offset, top + offset*i + offset, paint);
				}
			}
		}
	}
	
	/**
	 * Рисует внешний вид окна игры, включая счетчики и следующую фигуру
	 * @param canvas
	 */
	private void drawInterface(Canvas canvas) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);	//фон, черный
		paint.setColor(Color.BLACK);		
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		
		//----------( окно тетриса )------------------
		//серая рамка
		float grayOffset = 2;
		paint.setColor(Color.GRAY);
		canvas.drawRect(_windowLeft - grayOffset, _windowTop - grayOffset, _windowRight + grayOffset, _windowBottom + grayOffset, paint);		
		//игровое поле
		paint.setColor(Color.YELLOW);
		canvas.drawRect(_windowLeft, _windowTop, _windowRight, _windowBottom, paint);		
		
		//----------( правая панель )------------------
		float panelLeft = _windowRight + _offsetBorderX;
		float panelTop = _windowTop + _offsetBorderY;
		float verticalOffset = panelTop; // указывает смещение для каждого элемента, относительно вышестоящего элемента
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		
		//уровень
		paint.setColor(Color.WHITE);
		canvas.drawText("Level:", panelLeft, verticalOffset, paint);
		paint.setColor(Color.GREEN);
		verticalOffset += _offset*1.5f;
		canvas.drawText(Integer.toString(this.getLvl()), panelLeft + _offsetBorderX, verticalOffset, paint);
				
		//очки
		paint.setColor(Color.WHITE);
		verticalOffset += _offset*1.5f;
		canvas.drawText("Scores:", panelLeft, verticalOffset, paint);
		paint.setColor(Color.GREEN);
		verticalOffset += _offset*1.5f;
		canvas.drawText(Integer.toString(tetris.getScores()), panelLeft + _offsetBorderX, verticalOffset, paint);
		
		// следующая фигура
		paint.setColor(Color.WHITE);
		verticalOffset += _offset*2;
		canvas.drawText("Next shape:", panelLeft, verticalOffset, paint);
		verticalOffset += _offset;
		paintNextShape(canvas, panelLeft + _offsetBorderX*2, verticalOffset, _offset, paint);
		
		//максимальные очки
		if (tetris.getMaxScores() > 0) {
			paint.setColor(Color.WHITE);
			verticalOffset += _offset*4;
			canvas.drawText("Best result:", panelLeft, verticalOffset, paint);
			//verticalOffset += _offset*1.2f;
			//canvas.drawText("результат:", panelLeft, verticalOffset, paint); 
			paint.setColor(Color.GREEN);
			verticalOffset += _offset*1.5f;
			canvas.drawText(Integer.toString(tetris.getMaxScores()), panelLeft + _offsetBorderX, verticalOffset, paint);
		}
		
	} 
	
	/**
	 * Фигура которая будет следовать за текущей
	 * @param canvas
	 * @param x левая граница рисования
	 * @param y верхняя граница рисования
	 * @param offset расстояние между "точками" фигуры
	 * @param paint
	 */
	private void paintNextShape(Canvas canvas, float x, float y, float offset, Paint paint) {
		float curPosX = x;
		float curPosY = y;
		float radius = offset/2.3f;
		byte[][] tetCanv = tetris.getNextShape();

		for (int i = 0; i < tetCanv.length; i++) {
			curPosX = x;
			for (int j = 0; j < tetCanv[i].length; j++) {
				if (tetCanv[i][j] == 1) canvas.drawCircle(curPosX, curPosY, radius, paint);
				curPosX += offset;
			}
			curPosY += offset;
		}
	}
	
	/**
	 * Хрень которая должна показывать что пользователь выиграл
	 * @param canvas
	 */
	public void youWin(Canvas canvas) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);	//фон, черный
		paint.setColor(Color.RED);
		canvas.drawRect(40, 60, 80, 120, paint);
	}
	

	public void updateGame() {
		this.updateGame(false);
	}
	/**
	 * Основной метод расчетов поведения тетриса
	 * испльзуется непосредственно для определения вычисления состяония игры при визуализации
	 */
	public synchronized void updateGame(boolean moveDown) {		
		tetris.countScores();
		/*if (_tmp) {
			tetris.rotateShape();
			tetris.moveShape(1);
			tetris.moveShape(1);
			tetris.moveShape(1);
			tetris.moveShape(1);
			_tmp = false;
		}*/
		tetris.stepForward();
		if (moveDown) {
			tetris.moveDownShape();
		}
		
	}
	
	float startX;
	float startY;
	float postX;
	float postY;
	
	/**
	 * Взаимодействие с пользователем
	 * Движение вниз - ускоренное падение фигуры
	 * Короткое движение в стороны - перемещение фигуры
	 * Тап - вращение фигуры
	 */
	public boolean onTouchEvent(MotionEvent event) {
		int fallDownRange = 90;
		int moveAsideRange = 30;
		int rotateRange = 20;
       synchronized (_thread.getSurfaceHolder()) {
    	   
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
                   	startX = event.getX();
                   	startY = event.getY();
                   	break;
				case MotionEvent.ACTION_UP:
					float tmpX;
					float tmpY;
					postX = event.getX();
					postY = event.getY();
					tmpX = postX - startX;
					tmpY = postY - startY;
					if (Math.abs(tmpX) > moveAsideRange) {
						if (tmpX < 0) tetris.moveShape(TetrisCanvas.LEFT);
						if (tmpX > 0) tetris.moveShape(TetrisCanvas.RIGHT);
	                   	startX = 0;
	                   	postX = 0;
					} else if (tmpY > fallDownRange) {
						//быстрый спуск фигуры
						tetris.quickFall();
					} else if (Math.abs(tmpX) < rotateRange && Math.abs(tmpY) < rotateRange) {
						tetris.rotateShape();
					}
                   	break;                   	
			}            
       }
       return true;
    }
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}
	
	
	/**
	 * Создает область рисования, определяет всю начальную разметку и масштабирование игры
	 * + запускает бекграундный поток игры
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		_windowHeight = getHeight()* (7.0f/8.0f);		
		_offset = _windowHeight/TetrisCanvas.CANVAS_H;
		_windowWidth = _offset * TetrisCanvas.CANVAS_W;
		_offsetBorderY = (getHeight() - _windowHeight)/2;
		_offsetBorderX = getWidth()*2/100; // берем 2% от общей ширины канвы
		_windowTop = _offsetBorderY;
		_windowBottom = _windowHeight + _windowTop;
		_windowLeft = _offsetBorderX;
		_windowRight = _windowWidth + _windowLeft;
		_lvl = 1;
		_speed = 1000;
		
		_thread.setRunning(true);
		_thread.start();
		tetris.start();	
	}
	
	/**
	 * Метод пытается завершить бекграундный тред по уничтожении области рисования, тоесть при выходе из приложения
	 */
	
	public void killThread () {
		boolean retry = true;
		_thread.setRunning(false);
		while (retry) {
			try {
				_thread.join();
				retry = false;
			} catch (InterruptedException e) {}
		}
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		killThread();
	}
	
	/**
	 * Возвращает скорость падения фигуры
	 * @return
	 */
	public int getSpeed() {
		return _speed - _lvl*_speedStep;
	}
	
	/**
	 * Устанавливает скорость падения фигуры
	 * @param speed
	 */
	public void setSpeed(int speed) {
		_speed = speed;
	}
	
	/**
	 * Возвращает уровень сложности
	 * @return
	 */
	public int getLvl() {
		return _lvl;
	}
	
	/**
	 * Устанавливает уровень сложности игрока
	 * @param lvl
	 */
	public void setLvl(int lvl) {
		_lvl = lvl;
	}
	
	/**
	 * Проверяет не достиг ли игрок точки перехода на следующий уровень сложности, и если да, то переводит его
	 * увеличивает скорость падения фигур и поднимает уровень
	 */
	public void checkScores() {
		if (tetris.getScores() >= _lvl*_lvlStep) {
			_lvl++;
			//_speed -= _speedStep;
			//if (tetris.getScores() > tetris.getMaxScores())
			//	tetris.setMaxScores(tetris.getScores());
		}
	}

	/**
	 * Обнуляет весь процесс игры и записывает максимальный достигнутый результат
	 */
	public void newGame() {
		_lvl = 1;	
		tetris.start();
		//tetris.setMaxScores(tetris.getScores());
		//tetris.setScores(0);
		//_speed = _speed + _lvl*_speedStep;
			
		resumeGame();
		
	}
	
	public void pauseGame() {
		_thread.setPaused(true);
		_pauseCounter++;
		
	}
	
	public void resumeGame() {
		_pauseCounter--;
		if (_pauseCounter < 1) {
			_thread.setPaused(false);
		}
	}
	
	/**
	 * Проверка конца игры, победы или поражения
	 * @return
	 */
	public boolean isGameOver() {
		if (tetris.isGameOver() || _lvl >= MAX_LVL)
			return true;
		return false;
	}
	
	private float _windowHeight;
	private float _windowWidth;
	private float _windowTop;
	private float _windowLeft;
	private float _windowRight;
	private float _windowBottom;
	private float _offsetBorderX;
	private float _offsetBorderY;
	private float _offset;
	private int _lvl;
	private int _speed;
	private int _lvlStep = 1000;
	private int _speedStep = 20;
	public final static int MAX_LVL = 10;
	private int _pauseCounter = 0;
	
	//private boolean _tmp = true; // нужна для установки фигуры нужное положение при отладке
	
}
