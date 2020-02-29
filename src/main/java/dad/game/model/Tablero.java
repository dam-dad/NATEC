package dad.game.model;

import java.util.ArrayList;

import dad.App;
import dad.game.model.enums.AccionEnum;
import dad.game.model.enums.DireccionEnum;
import dad.game.model.enums.SujetoEnum;
import dad.game.model.enums.TipoEnum;

/**
 * Tablero del juego
 */
public class Tablero {
	
	private int cantidadColumnas;
	private int cantidadFilas;
	private Objeto<?>[][] objetos; // Mapa de objetos
	private ArrayList<Objeto<?>[][]> historial; // No implementado
	private ArrayList<Objeto<?>[]> frases;
	private ArrayList<Objeto<?>> elementosSegundoPlano;

	public Tablero() {
		this.historial = new ArrayList<Objeto<?>[][]>();
		this.frases = new ArrayList<Objeto<?>[]>();
		this.elementosSegundoPlano = new ArrayList<Objeto<?>>();
	}

	public Tablero(Objeto<?>[][] nivel) {
		this();
		this.cargarNivel(nivel);
	}

	public int getCantidadColumnas() {
		return cantidadColumnas;
	}

	public int getCantidadFilas() {
		return cantidadFilas;
	}

	public ArrayList<Objeto<?>[][]> getHistorial() {
		return historial;
	}

	public void setHistorial(ArrayList<Objeto<?>[][]> historial) {
		this.historial = historial;
	}

	public Objeto<?>[][] getPosicionObjetos() {
		return objetos;
	}
	
	public ArrayList<Objeto<?>> getElementosSegundoPlano() {
		return elementosSegundoPlano;
	}
	
	public int getCantidadFrases() {
		return frases.size();
	}

	/**
	 * Carga un nivel
	 * @param nivel Mapa de objetos
	 */
	public void cargarNivel(Objeto<?>[][] nivel) {
		this.cantidadFilas = nivel.length;
		this.cantidadColumnas = nivel[0].length;
		this.objetos = nivel;
		comprobarFrases();
		asignarEstados();
	}
	
	/**
	 * Método que mueve a los personajes (si se pudiese)
	 * @param direccion Dirección de movimiento
	 */
	public void mover(DireccionEnum direccion) {
		ArrayList<Objeto<?>> elementosYou = buscarElemento(AccionEnum.YOU); // Personajes (elementos con el estado YOU)

		if (elementosYou.size() > 0) {
			int[] direccionXY = direccionXY(direccion);
			for (Objeto<?> you : elementosYou) {
				int posObjetoYouX = you.getPosicion().getX();
				int posObjetoYouY = you.getPosicion().getY();
				int posNuevaPosicionX = posObjetoYouX + direccionXY[0];
				int posNuevaPosicionY = posObjetoYouY + direccionXY[1];
				// Comprobar límite del mapa
				if (comprobarBordeMapa(posNuevaPosicionX, posNuevaPosicionY)) {
					// Comprobar si la nueva posicion es STOP
					if (comprobarStop(you, direccionXY[0], direccionXY[1])) { // True no es STOP
						if (objetos[posNuevaPosicionY][posNuevaPosicionX] != null) { // Si no hay aire
							moverColindantes(direccion, you, false); // Mueve los objetos que tiene delante
						}
						// Si tiene aire delante puede moverse 
						if (objetos[posNuevaPosicionY][posNuevaPosicionX] == null) {
							objetos[posNuevaPosicionY][posNuevaPosicionX] = objetos[posObjetoYouY][posObjetoYouX]; // Se mueve
							objetos[posObjetoYouY][posObjetoYouX] = null;
							// Coloca el elemento que está en segundo plano en la posición anterior (si lo hubiese)
							for (Objeto<?> objetoSegundoPlano : elementosSegundoPlano) {
								if (objetoSegundoPlano.getPosicion().compararPosicion(you.getPosicion())) {
									objetos[posObjetoYouY][posObjetoYouX] = objetoSegundoPlano;
								}
							}
							// Se elimina del array de elementos si fue colocado en el mapa
							elementosSegundoPlano.remove(objetos[posObjetoYouY][posObjetoYouX]);
							you.getPosicion().mover(direccion, 1); // Actualiza la posición en el atributo del objeto
							comprobarDefeat(you);
							comprobarWin(you);
						}
					}
				}
			}
		}

		// mostrarTablero();
		comprobarFrases();
		//mostrarFrases();
		asignarEstados();
		// mostrarEstados();
		comprobarDefeat(null);
		comprobarWin(null);
	}

	/**
	 * Mueve de forma recursiva los objetos que son empujados
	 * @param direccion Dirección de movimiento
	 * @param objetoEmpujado Objeto que es empujado
	 * @param mover True: se desea mover el objeto; False: no queremos moverlo ya que es el objeto original que empuja y se mueve en el método "mover()"
	 */
	private void moverColindantes(DireccionEnum direccion, Objeto<?> objetoEmpujado, boolean mover) {
		int[] direccionXY = direccionXY(direccion);
		int posObjetoEmpujadoX = objetoEmpujado.getPosicion().getX();
		int posObjetoEmpujadoY = objetoEmpujado.getPosicion().getY();
		int posNuevaPosicionX = posObjetoEmpujadoX + direccionXY[0];
		int posNuevaPosicionY = posObjetoEmpujadoY + direccionXY[1];
		// Comprobar límite del mapa
		if (comprobarBordeMapa(posNuevaPosicionX, posNuevaPosicionY)) { 
			Objeto<?> nuevaPosicion = objetos[posNuevaPosicionY][posNuevaPosicionX];
			if (nuevaPosicion == null) { // En la siguiente posición hay aire
				objetos[posNuevaPosicionY][posNuevaPosicionX] = objetoEmpujado; // Se mueve
				objetos[posObjetoEmpujadoY][posObjetoEmpujadoX] = null;
				objetoEmpujado.getPosicion().mover(direccion, 1);
			} else { // En la siguiente posición no hay aire
				// Comprobar si la nueva posicion es STOP
				if (comprobarStop(objetoEmpujado, direccionXY[0], direccionXY[1])) { // True no es STOP
					boolean isPush = false; // TRUE si la siguiente posición puede ser empujado (tiene el estado PUSH)
					for (int i = 0; i < nuevaPosicion.getEstados().size(); i++) {
						if (nuevaPosicion.getEstados().get(i) == AccionEnum.PUSH) {
							isPush = true;
						}
					}
					// La nueva posicion puede ser empujada (tiene el estado PUSH o es una palabra)
					if (nuevaPosicion.getTipo() == TipoEnum.SUJETO || nuevaPosicion.getTipo() == TipoEnum.VERBO
							|| nuevaPosicion.getTipo() == TipoEnum.ACCION || isPush) {
						if (mover) { // Si se desea mover:
							// Llama de nuevo a este método para empujar el siguiente objeto
							moverColindantes(direccion, objetos[posNuevaPosicionY][posNuevaPosicionX], true);
							if (objetos[posNuevaPosicionY][posNuevaPosicionX] == null) { // Si la nueva posición ahora es aire:
								objetos[posNuevaPosicionY][posNuevaPosicionX] = objetoEmpujado; // Se mueve
								objetos[posObjetoEmpujadoY][posObjetoEmpujadoX] = null;
								objetoEmpujado.getPosicion().mover(direccion, 1); // Actualiza la posición en el atributo del objeto	
							}
						} else { // Si no deseamos que se mueva
							// Llama de nuevo a este método para empujar el siguiente objeto
							moverColindantes(direccion, objetos[posNuevaPosicionY][posNuevaPosicionX], true);
						}
					} else { // No se puede empujar, se colocará en el segundo plano
						elementosSegundoPlano.add(nuevaPosicion);
						objetos[posNuevaPosicionY][posNuevaPosicionX] = null;
						if (mover) { // Si se desea mover:
							objetos[posNuevaPosicionY][posNuevaPosicionX] = objetoEmpujado; // Se mueve
							objetos[posObjetoEmpujadoY][posObjetoEmpujadoX] = null;
							objetoEmpujado.getPosicion().mover(direccion, 1);
						}
					}
				}
			}
		}
	}

	/**
	 * Se comprueba si el objeto a mover tiene el estado STOP
	 * @param elementoAMover Elemento a mover
	 * @param direccionX Posición X a la que te mueves
	 * @param direccionY Posición Y a la que te mueves
	 * @return True no es STOP (puedes moverte); False es STOP (no puedes moverte)
	 */
	private boolean comprobarStop(Objeto<?> elementoAMover, int direccionX, int direccionY) {
		boolean stop = true;
		Objeto<?> elementoColision = objetos[elementoAMover.getPosicion().getY() + direccionY][elementoAMover
				.getPosicion().getX() + direccionX]; // Elemento siguiente del que se quiere mover
		if (elementoColision != null) { // Si no es aire:
			ArrayList<AccionEnum> estados = elementoColision.getEstados();
			for (int i = 0; i < estados.size(); i++) {
				if (estados.get(i) == AccionEnum.STOP) { // Se busca si tiene el estado STOP
					stop = false;
				}
			}
		}
		return stop;
	}

	/**
	 * Comprobar si el jugador pierde la partida
	 * @param you Objeto jugador (YOU)
	 */
	private void comprobarDefeat(Objeto<?> you) {
		boolean defeat = false;
		// Si los estados YOU y DEFEAT coinciden en el mismo elemento se pierde la partida
		ArrayList<Objeto<?>> elementosYou = buscarElemento(AccionEnum.YOU);
		if (elementosYou.size() > 0) {
			for (AccionEnum estado : elementosYou.get(0).getEstados()) {
				if (estado == AccionEnum.DEFEAT) {
					defeat = true;
				}
			}
		}
		// Si YOU está sobre el elemento DEFEAT se pierde la partida
		if (you != null) {
			for (Objeto<?> elemento : elementosSegundoPlano) {
				for (int i = 0; i < elemento.getEstados().size(); i++) {
					if (elemento.getEstados().get(i) == AccionEnum.DEFEAT
							&& elemento.getPosicion().compararPosicion(you.getPosicion())) {
						defeat = true;
					}
				}
			}
		}
		// Si no existe ningun elemento YOU se pierde la partida
		if (elementosYou.size() == 0) {
			defeat = true;
		}
		if (defeat) {
			App.getGameController().perder(); // Se avisa al controlador de que ha perdido la partida
		}
	}

	/**
	 * Comprobar si el jugador gana la partida
	 * @param you Objeto jugador (YOU)
	 */
	private void comprobarWin(Objeto<?> you) {
		boolean win = false;
		// Si los estados YOU y WIN coinciden en el mismo elemento se gana la partida
		ArrayList<Objeto<?>> elementosYou = buscarElemento(AccionEnum.YOU);
		if (elementosYou.size() > 0) {
			for (AccionEnum estado : elementosYou.get(0).getEstados()) {
				if (estado == AccionEnum.WIN) {
					win = true;
				}
			}
		}
		// Si YOU está sobre el elemento WIN se gana la partida
		if (you != null) {
			for (Objeto<?> elemento : elementosSegundoPlano) {
				for (int i = 0; i < elemento.getEstados().size(); i++) {
					if (elemento.getEstados().get(i) == AccionEnum.WIN
							&& elemento.getPosicion().compararPosicion(you.getPosicion())) {
						win = true;
					}
				}
			}
		}
		if (win) {
			App.getGameController().ganar(); // Se avisa al controlador de que ha ganado la partida
		}
	}

	/**
	 * Busca los elementos que tengan el estado que se pasa por parámetro
	 * @param estado Estado de los elementos a buscar
	 * @return Lista de elementos que cumplen la condición que se pasa por parámetro
	 */
	private ArrayList<Objeto<?>> buscarElemento(Object estado) {
		ArrayList<Objeto<?>> elementos = new ArrayList<Objeto<?>>();
		for (int i = 0; i < objetos.length; i++) {
			for (int j = 0; j < objetos[i].length; j++) {
				if (objetos[i][j] != null && objetos[i][j].getTipo() == TipoEnum.ELEMENTO) {
					for (int x = 0; x < objetos[i][j].getEstados().size(); x++) {
						if (objetos[i][j].getEstados().get(x) == estado) {
							elementos.add(objetos[i][j]);
						}
					}
				}
			}
		}
		return elementos;
	}

	/**
	 * Se convierte el enumerado de dirección en un número
	 * @param direccion Direccion de movimiento
	 * @return Array de dos posiciones: x, y (las posibilidades son 0, 1 o -1)
	 */
	private int[] direccionXY(DireccionEnum direccion) {
		int x = 0, y = 0;
		if (direccion == DireccionEnum.ARRIBA) {
			y = -1;
		} else if (direccion == DireccionEnum.ABAJO) {
			y = 1;
		} else if (direccion == DireccionEnum.DERECHA) {
			x = 1;
		} else if (direccion == DireccionEnum.IZQUIERDA) {
			x = -1;
		}
		return new int[] { x, y };
	}

	/**
	 * Comprueba que las coordenadas que se pasan por parámetros estén dentro del mapa
	 * @param x Coordenada X
	 * @param y Coordenada Y
	 * @return True si está dentro del mapa; False si está fuera del mapa
	 */
	private boolean comprobarBordeMapa(int x, int y) {
		return y >= 0 && y < cantidadFilas && x >= 0 && x < cantidadColumnas;
	}

	/**
	 * Comprueba las frases que están formadas en el mapa
	 */
	private void comprobarFrases() {
		frases.clear(); // Se borran las frases anteriores
		// Se recorren los objetos del mapa
		for (int i = 0; i < objetos.length; i++) {
			for (int j = 0; j < objetos[i].length; j++) {
				if (objetos[i][j] != null && objetos[i][j].getTipo() == TipoEnum.SUJETO) { // Si es un sujeto:
					// Comprobar hacia la derecha si se forma una frase
					if (j + 2 < cantidadColumnas) { // Comprobar si la posible frase está dentro del escenario
						if (objetos[i][j + 1] != null && objetos[i][j + 1].getTipo() == TipoEnum.VERBO) { // Si la siguiente posición es un verbo:
							if (objetos[i][j + 2] != null && (objetos[i][j + 2].getTipo() == TipoEnum.ACCION
									|| objetos[i][j + 2].getTipo() == TipoEnum.SUJETO)) { // Si la siguiente posición es una acción o un sujeto:
								Objeto<?>[] frase = { objetos[i][j], objetos[i][j + 1], objetos[i][j + 2] };
								frases.add(frase); // Se guarda la frase
							}
						}
					}
					// Comprobar hacia abajo si se forma una frase
					if (i + 2 < cantidadFilas) { // Comprobar si la posible frase está dentro del escenario
						if (objetos[i + 1][j] != null && objetos[i + 1][j].getTipo() == TipoEnum.VERBO) { // Si la siguiente posición es un verbo:
							if (objetos[i + 2][j] != null && (objetos[i + 2][j].getTipo() == TipoEnum.ACCION
									|| objetos[i + 2][j].getTipo() == TipoEnum.SUJETO)) { // Si la siguiente posición es una acción o un sujeto:
								Objeto<?>[] frase = { objetos[i][j], objetos[i + 1][j], objetos[i + 2][j] };
								frases.add(frase); // Se guarda la frase
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Se asignan los estados a los sujetos o se convierte los sujetos a otros sujetos
	 */
	private void asignarEstados() {
		ArrayList<Objeto<?>> elementos = new ArrayList<Objeto<?>>();
		// Coge todos los elementos y borra los estados
		for (int i = 0; i < objetos.length; i++) {
			for (int j = 0; j < objetos[i].length; j++) {
				Objeto<?> elemento = objetos[i][j];
				if (elemento != null && elemento.getTipo() == TipoEnum.ELEMENTO) {
					elemento.limpiarEstados();
					elementos.add(elemento);
				}
			}
		}
		// Se asignan a los elemento los estados de las frases formadas
		for (Objeto<?>[] frase : frases) {
			for (Objeto<?> elemento : elementos) {
				// frase[0] = sujeto; frase[1] = verbo; frase[2] = accion/sujeto a convertir
				if (elemento.getNombre() == frase[0].getNombre()) {
					if(frase[2].getNombre() instanceof AccionEnum) { // Si es una acción, se agrega el estado
						elemento.setEstado((AccionEnum) frase[2].getNombre());
					} else { // Si es un sujeto, se convierte a ese sujeto
						@SuppressWarnings("unchecked")
						Objeto<SujetoEnum> conversor = (Objeto<SujetoEnum>) frase[2];
						elemento.setNombre((SujetoEnum) conversor.getNombre());
					}
					
				}
			}
		}
	}

	/**
	 * Mostrar las frases formadas por consola
	 */
	@SuppressWarnings("unused")
	private void mostrarFrases() {
		System.out.println("Frases: ");
		for (int i = 0; i < frases.size(); i++) {
			System.out.println(frases.get(i)[0].getNombre() + " " + frases.get(i)[1].getNombre() + " "
					+ frases.get(i)[2].getNombre());
		}
	}

	/**
	 * Mostrar los estados de los elementos por consola
	 */
	@SuppressWarnings("unused")
	private void mostrarEstados() {
		System.out.println("Estados: ");
		for (int i = 0; i < objetos.length; i++) {
			for (int j = 0; j < objetos[i].length; j++) {
				Objeto<?> elemento = objetos[i][j];
				if (elemento != null && elemento.getTipo() == TipoEnum.ELEMENTO) {
					System.out.println(elemento.getNombre() + " => " + elemento.getEstados());
				}
			}
		}
	}

	/**
	 * Mostrar el tablero por consola
	 */
	@SuppressWarnings("unused")
	private void mostrarTablero() {
		System.out.println("Tablero: ");
		for (int i = 0; i < objetos.length; i++) {
			for (int j = 0; j < objetos[i].length; j++) {
				if (objetos[i][j] != null) {
					System.out.print(objetos[i][j].toString());
				} else {
					System.out.print("·");
				}
			}
			System.out.println();
		}
	}
}
