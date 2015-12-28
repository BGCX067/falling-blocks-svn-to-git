package ru.tomsk.pear.tetris;

import java.util.Random;

public class TetrisShape {
	
	public TetrisShape() {
		rnd = new Random(System.currentTimeMillis());
		canvas = new byte[CANVAS_H][CANVAS_W];
		setNextShape();		
		setNewShape();
		shPlace = new ShapePlace();
	}
	
	/**
	 * очищает матрицу тетриса
	 */
	private void eraseCanvas() {
		for (int i = 0; i < CANVAS_H; i++) {
			for (int j = 0; j < CANVAS_W; j++) {
				canvas[i][j] = 0;
			}
		}
	}
	
	/**
	 * устанавливает номер следующей фигуры 
	*/
	public void setNextShape() {
		nextShape = (byte)rnd.nextInt(shapes.length);
	}
	
	/**
	 * Устанавливает текущюю фигуру из списка shapes 
	*/
	public void setNewShape() {
		eraseCanvas();
		sumShapeCanvas(shapes[nextShape]);
		setNextShape();
		curY = 0;
	}
	
	/**
	 * проверяет выйдет ли фигура за границы или пересечется ли с другими элементами при повороте
	 * @param canv
	 * @return
	 */
	public boolean isRichBorderOnRotate(byte[][] canv) {
		/*shPlace = */findShapeOnCanvas(canvas);
		int shapeH = shPlace.bottom - shPlace.top + 1;
		int shapeW = shPlace.right - shPlace.left + 1;
		
		//byte[][] tmp = new byte[shapeW][shapeH];
		if ((shPlace.left + shapeH) >= TetrisShape.CANVAS_W) return true;
		if ((shPlace.top + shapeW) >= TetrisShape.CANVAS_H) return true;
		for (int i = 0; i < shapeH ; i++) {
			for (int j = 0; j < shapeW; j++) {			
				if ((canv[j + shPlace.top][i + shPlace.left] == 1) && (canvas[i + shPlace.top][j + shPlace.left] == 1)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Поворачивает фигуру вправо
	*/
	public void rotate() {
		/*shPlace = */findShapeOnCanvas(canvas);
		int shapeH = shPlace.bottom - shPlace.top + 1;
		int shapeW = shPlace.right - shPlace.left + 1;
		byte[][] tmp = new byte[shapeH][shapeW];
		for (int i = 0; i < shapeH ; i++) {
			for (int j = 0; j < shapeW; j++) {
				tmp[i][j] = canvas[i + shPlace.top][j + shPlace.left];
				canvas[i + shPlace.top][j + shPlace.left] = 0;
			}
		}
		for (int i = 0; i < shapeW ; i++) {
			for (int j = 0; j < shapeH; j++) {
				canvas[i + shPlace.top][j + shPlace.left] = tmp[Math.abs(j - shapeH + 1)][i]; 
			}
		}
	}
	
	/**
	 * Лепит вместе маленькую канву фигуры и основную канву фигуры
	 * @param shp
	 */
	private void sumShapeCanvas(byte[][] shp) {
		for (int i = 0; i < shp.length; i++) {
			for (int j = 0; j < shp[0].length; j++) {
				canvas[i][j + canvas[0].length/2] = shp[i][j];
			}
		}
	} 
	/**
	 * Клонирует матрицу 
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
	 * возвращает следующую фигуру
	*/
	public byte[][] getNextShape() {
		return cloneMatrix(shapes[nextShape]);
	}
	
	/**
	 * возвращает текущую фигуру
	*/
	public byte[][] getCanvas() {
		return cloneMatrix(canvas);
	}
		
	
	public void moveDownShape() {
		byte[][] tmp = cloneMatrix(canvas);
		
		for (int i = 0; i < tmp.length; i++) {
			for (int j = 0; j < tmp[0].length; j++) {
				canvas[(i + 1) % canvas.length][j] = tmp[i][j];
			}
		}
		curY++;
	}
	
	/**
	 * Сдвиг фигуры влево или вправо
	*/
	public void moveShape(int dir) {
		if (dir == TetrisShape.LEFT || dir == TetrisShape.RIGHT) {
			findShapeOnCanvas(canvas);
			byte[][] tmp = cloneMatrix(canvas);
			
			//if (!isRichBorder(dir, shPlace, canvas)) {
				/*
				 * магические цифры + и - 1 нужны чтобы расширить 
				 * диапазон перерисовываемой
				 * фигуры, чтобы стирать ее предыдущее положение
				 */
				for (int i = shPlace.top; i <= shPlace.bottom; i++) {
					for (int j = shPlace.left - 1; j <= shPlace.right + 1; j++) {
						canvas[i][(j + dir + canvas[0].length) % canvas[0].length] = tmp[i][(j + canvas[0].length) % canvas[0].length];
					}
				//}
			} 
		}
	}
	/**
	 * Проверяет достигла ли фигура края или других фигур
	 * @param dir направление (-1 и 1)
	 * @param canv канва поля с упавшими фигурами
	 * @return
	 */
	public boolean isRichBorder(int dir, byte[][] canv) {
		findShapeOnCanvas(canvas);
		if (TetrisShape.LEFT == dir) {
			if ((shPlace.left == 0)) return true;
		} else if (TetrisShape.RIGHT == dir) {
			if ((shPlace.right == canvas[0].length - 1)) return true;
		}
		/*
		 * Магически цифры для проверки областей прилежащих к фигуре
		 */
		if (TetrisShape.LEFT == dir) {
			for (int i = shPlace.top; i <= shPlace.bottom; i++) {
				if (canv[i][shPlace.left - 1] == 1) return true; 
			}
		} else if (TetrisShape.RIGHT == dir) {
			for (int i = shPlace.top; i <= shPlace.bottom; i++) {
				if (canv[i][shPlace.right + 1] == 1) return true; 
			}
		}
		return false;
	}
	
	/**
	 * Ищет кординаты контура фигуры, возвращает ShapePlace с координатами краев фигуры
	*/
	
	private void findShapeOnCanvas(byte[][] canv) {
		shPlace.clean();
		boolean x = false;
		boolean y = false;
		
		// просматриваем не всю матрицу а только начиная с верзней точки фигуры
		for (int i = curY; i < canv.length; i++) {
			x = false;
			for (int j = 0; j < canv[0].length; j++) {
				if ((canv[i][j] == 1) && !x && (shPlace.left > j)) {
					shPlace.left = j;
					x = true;
					/*
					 * если же у нас фигура типа "палка", то
					 */
					if ((j + 1) > TetrisShape.CANVAS_W && (canv[i][j + 1] == 0) && (shPlace.right < shPlace.left)) {
						shPlace.right = shPlace.left;
					}
				} else if ((canv[i][j] == 1) && (shPlace.right < j)) {
					shPlace.right = j;
				}
				if ((!y && canv[i][j] == 1)) {
					shPlace.top = i;
					y = true;
					/*
					 * если же у нас фигура типа "палка", то
					 */
					if ((i + 1) > TetrisShape.CANVAS_H && (canv[i + 1][j] == 0) && (shPlace.bottom < shPlace.top)) {
						shPlace.bottom = shPlace.top;
					}
				} else if (canv[i][j] == 1) {
					shPlace.bottom = i;
				}
			}
		}
	}
	
	/**
	 * Возвращает координаты фигуры в канве
	 */
	public ShapePlace getShapePlace() {
		this.findShapeOnCanvas(canvas);
		return shPlace;
	}
	
	/**
	 * роняет фигуру вниз
	 * @param canv канва с упавшими фигурами
	 */
	public void quickFall(byte[][] canv) {
		boolean foundBottom = false; // нужна чтобы прекратить поиск ботома на канве упавших фигру
		int bottom = canv.length - 1; // предполагаем что у нас вообще нет упавших фигур
		this.findShapeOnCanvas(canvas);
		
		// для начала копируем исходную фигуру
		byte[][] tmpShape = new byte[shPlace.bottom - shPlace.top + 1][shPlace.right - shPlace.left + 1];
		for (int i = 0; i < tmpShape.length; i++) {
			for (int j = 0; j < tmpShape[0].length; j++) {
				tmpShape[i][j] = canvas[i + shPlace.top][j + shPlace.left];
				canvas[i + shPlace.top][j + shPlace.left] = 0;
			}
		}	
		
		//ищем ботом
		for (int i = shPlace.top; i < canv.length; i++) {
			for (int j = shPlace.left; j <= shPlace.right; j++) {
				if (canv[i][j] == 1)  { 
					bottom = i - 1;
					foundBottom = true;
					break;
				}
			}
			if (foundBottom) break;
		}
		
		//кладем фигуру на пол
		for (int i = tmpShape.length - 1; i >= 0 ; i--) {
			for (int j = 0; j < tmpShape[0].length; j++) {
				canvas[bottom + i - tmpShape.length/* - (shPlace.right - shPlace.left + 2)*/][j + shPlace.left] = tmpShape[i][j];
			}
		}
	}
	
	public static final byte SHAPE_H = 3; // высота полотна фигуры
	public static final byte SHAPE_W = 3; // шириа полотна фигуры
	public static final int CANVAS_H = 22;
	public static final int CANVAS_W = 10;
	
	private byte[][] canvas; // канва с фигурой
	private byte nextShape;	// следующая фигура
	public static final byte LEFT = -1; // константы для сдвига фигур в сторону
	public static final byte RIGHT = 1; //
	private ShapePlace shPlace; // координаты текущей фигуры
	private int curY; // текущее положение верхней границы фигуры на большой канве
	
	
	private Random rnd; // выбирает следующую фигуру, откровенно хреновый рандом
	
	// шаблоны фигур
	private byte[][][] shapes = {
		{	
			{0,1,0},
			{0,1,0},
			{0,1,0}
		},
		{	
			{1,1,0},
			{0,1,1},
			{0,0,0}
		},
		{	
			{0,1,1},
			{1,1,0},
			{0,0,0}
		},
		{	
			{0,1,0},
			{1,1,1},
			{0,0,0}
		},
		{	
			{1,0,0},
			{1,1,1},
			{0,0,0}
		},
		{	
			{0,0,1},
			{1,1,1},
			{0,0,0}
		},
		{	
			{1,1,0},
			{1,1,0},
			{0,0,0}
		},
	};
	
	// вспомогательный класс для координат фигуры на канве
	public class ShapePlace {
		public ShapePlace() {
			top = TetrisShape.CANVAS_H; // up is down
			bottom = 0;
			left = TetrisShape.CANVAS_W; 
			right = 0;
		}
		
		/**
		 * "обнуляет" координаты
		 */
		public void clean() {
			top = TetrisShape.CANVAS_H;
			bottom = 0;
			left = TetrisShape.CANVAS_W; 
			right = 0;
		}
		public int top;
		public int bottom;
		public int left;
		public int right;
	}
}
