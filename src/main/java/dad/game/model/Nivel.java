package dad.game.model;

public class Nivel {
	
	public static Objeto[][] uno() {
		String[] nivel= {
				"························",
				"························",
				"··········diy···········",
				"························",
				"····WWWWWWWWWWWWWWWW····",
				"························",
				"·····D············F·····",
				"························",
				"····WWWWWWWWWWWWWWWW····",
				"························",
				"··········fiw···········",
				"························",
				"··········pis···········",
				"··········i·············",
				"··········m·············"
		};
		return parsear(nivel);
	}
	
	/**
	 * 
	 * @param nivel
	 * @return
	 */
	private static Objeto[][] parsear(String[] nivel) {
		Objeto[][] mapa = new Objeto[nivel.length][nivel[0].length()];
		for (int i = 0; i < nivel.length; i++) {
			for (int j = 0; j < nivel[i].length(); j++) {
				char letra = nivel[i].charAt(j);
				Posicion posicion = new Posicion(j, i);
				if(letra == '·') { // Aire
					mapa[i][j] = null;
				} else if (letra == 'i') {
					mapa[i][j] = new Objeto<VerboEnum>(VerboEnum.IS, TipoEnum.VERBO, letra, posicion);
				} else if (letra == 'W') {
					mapa[i][j] = new Objeto<SujetoEnum>(SujetoEnum.WALL, TipoEnum.ELEMENTO, letra, posicion);
				} else if (letra == 'F') {
					mapa[i][j] = new Objeto<SujetoEnum>(SujetoEnum.FLAG, TipoEnum.ELEMENTO, letra, posicion);
				} else if (letra == 'D') {
					mapa[i][j] = new Objeto<SujetoEnum>(SujetoEnum.DAD, TipoEnum.ELEMENTO, letra, posicion);
				} else if (letra == 'd') {
					mapa[i][j] = new Objeto<SujetoEnum>(SujetoEnum.DAD, TipoEnum.SUJETO, letra, posicion);
				} else if (letra == 'f') {
					mapa[i][j] = new Objeto<SujetoEnum>(SujetoEnum.FLAG, TipoEnum.SUJETO, letra, posicion);
				} else if (letra == 'p') {
					mapa[i][j] = new Objeto<SujetoEnum>(SujetoEnum.WALL, TipoEnum.SUJETO, letra, posicion);
				} else if (letra == 'y') {
					mapa[i][j] = new Objeto<AccionEnum>(AccionEnum.YOU, TipoEnum.ACCION, letra, posicion);
				} else if (letra == 'w') {
					mapa[i][j] = new Objeto<AccionEnum>(AccionEnum.WIN, TipoEnum.ACCION, letra, posicion);
				} else if (letra == 's') {
					mapa[i][j] = new Objeto<AccionEnum>(AccionEnum.STOP, TipoEnum.ACCION, letra, posicion);
				} else if (letra == 'm') {
					mapa[i][j] = new Objeto<AccionEnum>(AccionEnum.DEFEAT, TipoEnum.ACCION, letra, posicion);
				}
				//Añadir posición cuando no sea aire
				if(mapa[i][j] != null) {
					mapa[i][j].setPosicion(posicion);
				}
				
			}
		}
		
		return mapa;
	}
	
	
}
