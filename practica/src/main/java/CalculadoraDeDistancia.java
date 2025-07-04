public class CalculadoraDeDistancia {

    public static void main(String[] args) {
        // Coordenadas del jugador y del árbol
        double jugadorX = 10.0;
        double jugadorZ = 20.0;
        double arbolX = 15.5;
        double arbolZ = 25.5;

        // Calculamos la distancia en cada eje
        double distanciaX = arbolX - jugadorX; // Resultado: 5.0
        double distanciaZ = arbolZ - jugadorZ; // Resultado: 5.0

        // Aplicamos el Teorema de Pitágoras: distancia = raíz_cuadrada(a^2 + b^2)

        // Usamos Math.pow() para elevar al cuadrado
        double sumaDeCuadrados = Math.pow(distanciaX, 2) + Math.pow(distanciaZ, 2);

        // Usamos Math.sqrt() para la raíz cuadrada
        double distanciaFinal = Math.sqrt(sumaDeCuadrados);

        System.out.println("La distancia en línea recta hasta el árbol es: " + distanciaFinal + " bloques.");
        // El resultado será aproximadamente 7.07
    }
}