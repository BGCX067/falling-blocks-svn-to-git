package ru.tomsk.pear.tetris;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class TetrisThread extends Thread {
	public TetrisThread(SurfaceHolder h, Game g) {
		_holder = h;
		_game = g;	
	}
	
	/**
	 * Главное условие работы игры
	 * @param run
	 */
	public void setRunning(boolean run) {
		_run = run;
	}
	public void setPaused(boolean paused) {
		_paused = paused;
	}
	
	public void run() {
		Canvas c;
		int ticks = 0;
		_game.setSpeed(300); // устанавливаю тут потому что не знаю сейчас как установить не в нативном треде параметр, просто так не устанавливается через конструктор
		while (_run) {
			try {
				Thread.sleep(SKIP_TICKS);// ждем 1000/FPS миллисекунд
			} catch (InterruptedException e) {}
			
			if (!_paused) {
			c = null;
			try {		
				//Thread.sleep(SKIP_TICKS);// ждем 1000/FPS миллисекунд
				
				// если по времени пора опускать фигуру - делаем это, если нет только расчитываем поворот и сдвиг в стороны
				if ((ticks*SKIP_TICKS) > _game.getSpeed()) {
					_game.updateGame(true);
					ticks = 0;// ждем следующего периода для смещения фигуры вниз
				} else {
					_game.updateGame();
				}
				
				_game.checkScores();
				
				c = _holder.lockCanvas(null); //лочим канву так чтобы никто кроме этого треда в нее не лез				
				synchronized (_holder) {
					_game.onDraw(c);
					// если игрок выиграл, говорим ему об этом, если проиграл - нет и сбрасываем все счетчики
					if (_game.isGameOver()) {
						/*if (_game.getLvl() >= Game.MAX_LVL) {
							_game.youWin(c);
							Thread.sleep(2000); // на 2 секунды замираем чтобы дать прочитать сообщение
						}*/
						_game.newGame();
					}
				}
				ticks++;	
								
				
			} /*catch (InterruptedException e) {
				//ножно для слипа треда
			}*/ finally {
				_holder.unlockCanvasAndPost(c); //рисуем канву
			}
		}
		}
	}

	public SurfaceHolder getSurfaceHolder() {
        return _holder;
    }
	
	SurfaceHolder _holder;
	Game _game;
	private boolean _run = false;
	private boolean _paused = false; 
	//private int MAX_TICKS = 10;
	private int SECOND = 1000; // просто секунда
	private int FPS = 15; //кадры в секунду
	private int SKIP_TICKS = SECOND/FPS;// интервалы между расчетами с отрисовкой
}
