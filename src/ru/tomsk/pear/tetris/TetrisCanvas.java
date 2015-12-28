package ru.tomsk.pear.tetris;

import java.util.ArrayList;

public class TetrisCanvas {
	
	public TetrisCanvas() {
		canvas = new byte[CANVAS_H][CANVAS_W];
		shape = new TetrisShape();
		
	}
	
	public void start() {
		eraseCanvas();
		shape.setNewShape();
		if (scores > maxScores) {
			maxScores = scores; 
		}
		scores = 0;
		//fillCanvas();
	}
	
	/**
	 * Полностью очищает главную канву
	*/
	private void eraseCanvas() {
		for (int i = 0; i < CANVAS_H; i++) {
			for (int j = 0; j < CANVAS_W; j++) {
				canvas[i][j] = 0;
			}
		}
	}
	
	/**
	 * Заполняет главную канву единицами, нужно для теста
	*/
	public void fillCanvas() {
		for (int i = 16; i < 19; i++) {
			for (int j = 0; j < CANVAS_W; j++) {
				if ((j % 7) != 0)canvas[i][j] = 1;
				
			}
		}
	}
	
	/**
	 * клонирует указанную канву
	*/
	private byte[][] cloneMatrix(byte[][] matrix) {
		byte[][] tmp = new byte[matrix.length][matrix[0].length];
	
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				tmp[i][j] = matrix[i][j];
			}
		}
		return tmp;
	}
	
	
	/**
	 * Возвращает канву с текущей фигурой
	*/
	public byte[][] getCurCanvas() {	
		byte[][] resultCanvas = cloneMatrix(canvas);

		resultCanvas = sumShapeCanvas(resultCanvas);	
		return resultCanvas;
	}
	
	public void stepForward() {
		byte[][] resultCanvas = cloneMatrix(canvas);
		resultCanvas = sumShapeCanvas(resultCanvas);
		if (shapeAtBottom()) {
			canvas = cloneMatrix(resultCanvas);
			shape.setNewShape();
		} //else {
		//	shape.moveDownShape();
		//}		
	} 
	
	public void moveDownShape() {
		shape.moveDownShape();
	}
	
	/**
	 * Добавляет фигуру к УКАЗАННОЙ канве, указанной в параметре
	*/
	private byte[][] sumShapeCanvas(byte[][] canv) {
		byte[][] tmp = shape.getCanvas();
		
		for (int i = 0; i < canv.length; i++) {
			for (int j = 0; j < canv[0].length; j++) {
				canv[i][j] = ((tmp[i][j] == 1) || (canv[i][j] == 1 )) ? (byte)1 : (byte)0;
			}
		}
		return canv;
	}
	
	/**
	 * Лежит ли фигура на дне поля 
	 */
	private boolean shapeAtBottom() {
		byte[][] tmp = shape.getCanvas();
		
		for (int i = 0; i < canvas.length - 1; i++) {
			for (int j = 0; j < canvas[0].length; j++) {
				if ((canvas[i + 1][j] == 1) && (tmp[i][j] == 1)) return true;
			}
		}
		
		for (int i = 0; i < canvas[0].length; i++) {
			if (tmp[canvas.length - 1][i] == 1) return true;
		}
		
		return false;
	}
	
	/**
	 * признак конца игры 
	 */
	public boolean isGameOver() {
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < canvas[0].length; j++) {
				if (canvas[i][j] == 1) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Находит строки полностью занятые единицами
	*/
	private ArrayList<Integer> parseLines() {
		ArrayList<Integer> lines = new ArrayList<Integer>();
		int count = 0;
		for (int i = 0; i < CANVAS_H; i++) {
			count = 0;
			for (int j = 0; j < CANVAS_W; j++) {
				if (canvas[i][j] == 1) {
					count++;
				}
			}
			if (count == CANVAS_W) {
				lines.add(i);
			}
		}
		return lines;
	}
	
	public void countScores() {
		ArrayList<Integer> lines = parseLines();
		if (lines.size() > 0) {
			for (Integer line : lines) {
				eraseLine(line);
				scores += incScoresStep;
			}
		}
	}
	
	private void eraseLine(int line) {
		
		byte[][] tmp = cloneMatrix(canvas);
		for (int i = 0; i < line; i++) {
			for (int j = 0; j < TetrisCanvas.CANVAS_W; j++)
				canvas[i + 1][j] = tmp[i][j];
		}
		for (int i = 0; i < TetrisCanvas.CANVAS_W; i++) {
				canvas[0][i] = 0;//tmp[line][i];
		}
	}
	
		
	public int getScores() {
		return scores;
	}
	
	public void setMaxScores(int scr) {
		if (maxScores < scr)
			maxScores = scr; 
	}
	
	public int getMaxScores() {
		return maxScores; 
	}
	
	public void setScores(int scr) {
		scores = scr;
	}
	
	public byte[][] getNextShape() {
		return shape.getNextShape();
	}
	
	/**
	 * Возвращает базовую канву
	*/
	
	public void moveShape(int dir) {
		if (!shape.isRichBorder(dir, canvas))
			shape.moveShape(dir);
	}
	
	public void rotateShape() {
		if (!shapeAtBottom() && !shape.isRichBorderOnRotate(canvas))
			shape.rotate();
	}
	
	public void quickFall() {
		if (!shapeAtBottom()) {
			shape.quickFall(canvas);
		}
	}
	
	public byte[][] getCanvas() {
		return cloneMatrix(canvas);		
	}	
	
	//--------------------
	public static final byte CANVAS_H = TetrisShape.CANVAS_H;
	public static final byte CANVAS_W = TetrisShape.CANVAS_W;
	public static final byte LEFT = -1;
	public static final byte RIGHT = 1;
	private byte[][] canvas;
	private int scores = 0;
	private int maxScores = 0;
	private int incScoresStep = 100;
	private TetrisShape shape;

	
}
