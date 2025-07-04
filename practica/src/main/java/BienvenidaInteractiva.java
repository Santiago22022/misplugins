import java.util.Scanner;

// Paso 1: Importamos la caja de herramientas
public class BienvenidaInteractiva
{


    public static void main(String[] args) {
        // Paso 2: Creamos nuestro "oído" y lo conectamos al teclado
        Scanner teclado = new Scanner(System.in);

        // Pedimos información al usuario
        System.out.println("¡Hola, futuro programador! Por favor, introduce tu nombre:");

        // Paso 3: Usamos nextLine() para leer el texto que el usuario escriba
        String nombreUsuario = teclado.nextLine();

        System.out.println("¡Un gusto conocerte, " + nombreUsuario + "! Ahora, dime tu edad:");

        // Usamos nextInt() para leer el número que el usuario escriba
        int edadUsuario = teclado.nextInt();

        // Respondemos con la información que recolectamos
        System.out.println("¡Genial! Tienes " + edadUsuario + " años. ¡Bienvenido a Java!");

        // Es una buena práctica cerrar el "oído" cuando ya no lo usas.
        teclado.close();
    }
}