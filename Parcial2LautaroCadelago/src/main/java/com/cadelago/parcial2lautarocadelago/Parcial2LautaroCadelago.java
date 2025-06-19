package com.cadelago.parcial2lautarocadelago;

import java.util.Scanner;
import java.util.Random;

public class Parcial2LautaroCadelago {

    // CALCULAR VALOR DE LA CARTA
    public static int calcularValorCarta(int numeroCarta, int puntajeActual) {
        switch (numeroCarta) {
            case 1: // AS
                // Si suma 11 no te pasa de 21, el AS vale 11. Si no, vale 1.
                return (puntajeActual + 11 <= 21) ? 11 : 1;
            case 10: // 10
            case 11: // J (Jota)
            case 12: // Q (Reina)
            case 13: // K (Rey)
                return 10;
            default:
                return numeroCarta;
        }
    }

    // Función para "mezclar" el mazo manualmente (Algoritmo de Fisher-Yates)
    public static void mezclarMazo(int[] arrayCartas, Random mezclaRandom) {
        int cantidadCartas = arrayCartas.length;
        for (int i = cantidadCartas; i > 1; i--) {
            // Elegimos un índice aleatorio entre 0 y i-1
            int posicionRandom = mezclaRandom.nextInt(i); 
            // Intercambiamos el elemento actual con el elemento en el índice aleatorio
            int cartaTemporal = arrayCartas[i - 1];
            arrayCartas[i - 1] = arrayCartas[posicionRandom];
            arrayCartas[posicionRandom] = cartaTemporal;
        }
    }

    // Función auxiliar para obtener el nombre de la carta (AS, J, Q, K)
    public static String nombreCarta(int numeroCarta) { 
        switch (numeroCarta) {
            case 1:
                return "AS";
            case 11:
                return "J"; // Jota
            case 12:
                return "Q"; // Reina
            case 13:
                return "K"; // Rey
            default:
                return String.valueOf(numeroCarta);
        }
    }

    public static void main(String[] args) {

        Scanner entrada = new Scanner(System.in);
        Random numeroRandom = new Random(); 

        String nombreJugador;
        int puntajeJugador = 0;         // Puntos del jugador
        int puntajeBanca = 0;           // Puntos de la banca
        int cartaActual;                // Carta que se saca en el momento
        double dineroDisponible;        // Dinero para apostar
        double apuestaActual;           // Dinero apostado en la ronda
        String respuestaUsuario = "";   // "S/N/R" para las decisiones del jugador (Sí, No, Recargar)
        boolean jugadorSePaso = false;
        boolean jugadoBlackjackServido = false; 
        boolean apuestaValida = false;

        // Variables para la gestión de los mazos (el "shoe" o mezclador de cartas)
        int cantidadMazos = 0; // Inicializamos a 0 para el bucle de validación
        int[] mezclador; // Usamos un array de enteros para representar el conjunto de mazos
        int indiceActualMezclador; // Índice para saber qué carta sigue en el mazo grande
        // Umbral para decidir cuándo mezclar de nuevo (ej: si quedan menos de 52 cartas)
        int minimoCartasAMezclar = 52; // Usamos int normal sin 'final'

        // INTRO Y REGLAS DEL JUEGO
        System.out.println("Bienvenido al Blackjack!");
        System.out.println("-------------------------");
        System.out.println("Objetivo del juego: Sumar 21 o acercarse lo más posible sin pasarse. Jugas contra la banca.");
        System.out.println("Valores de las cartas: 2-10 valen su numero, Figuras (J,Q,K) valen 10. El AS vale 1 u 11.");
        System.out.println("BLACKJACK SERVIDO: AS + carta de 10 puntos en las dos primeras cartas. Se paga triple!");
        System.out.println("Como se juega: Apuesta inicial. Se reparten 2 cartas a vos y 1 a la banca.");
        System.out.println("Tu turno: Podes pedir carta o plantarte. Si te pasas de 21, perdes.");
        System.out.println("Turno de la banca: Pide cartas hasta sumar 17 o mas.");
        System.out.println("Resultados:");
        System.out.println(" - Ganas si tenes mas puntos que la banca (sin pasarte) o si la banca se pasa. Se paga doble");
        System.out.println(" - Empate: Recuperas tu apuesta.");
        System.out.println(" - Perdes si te pasas de 21 o la banca tiene mas puntos (sin pasarse).");
        System.out.println("-------------------------");


        // --- INICIO HARDCODEO PARA DEMO (COMENTADO) ---

        // // Hardcodeo de nombre de jugador
        // // System.out.println("Ingresa tu nombre:");
        // // nombreJugador = entrada.nextLine();
        // nombreJugador = "DemoPlayer";
        // System.out.println("Nombre del jugador hardcodeado para demo: " + nombreJugador);
        //
        // // Hardcodeo de dinero inicial
        // // do { ... } while (dineroDisponible > 50000 || dineroDisponible <= 0);
        // dineroDisponible = 1000.0;
        // System.out.println("Dinero inicial hardcodeado para demo: $" + dineroDisponible);
        //
        // System.out.println("-------------------------");
        //
        // // Hardcodeo de cantidad de mazos
        // // do { ... } while (!cantidadMazosValida);
        // cantidadMazos = 2; // Suficiente para las secuencias de demo
        // System.out.println("Cantidad de mazos hardcodeada para demo: " + cantidadMazos);

        // --- FIN HARDCODEO PARA DEMO (COMENTADO) ---


        // Validar el nombre del jugador (DESCOMENTADO)
        do {
            System.out.println("Ingresa tu nombre:");
            nombreJugador = entrada.nextLine(); // Leemos el nombre completo
            if (nombreJugador.isEmpty()) {
                System.out.println("ERROR! El nombre no puede estar vacío. Por favor, ingresa un nombre válido.");
            }
        } while (nombreJugador.isEmpty());


        // INGRESAR DINERO INICIAL Y VALIDAR (DESCOMENTADO)
        do {
            System.out.print(nombreJugador + ", ingresa tu dinero para empezar a jugar (entre $1 y $50.000): $");
            String dineroIngresado = entrada.next();
            try {
                dineroDisponible = Double.parseDouble(dineroIngresado);
                if (dineroDisponible > 50000) {
                    System.out.println("ERROR! No podes cargar mas de $50.000. Por favor " + nombreJugador + ", ingresa un monto menor");
                } else if (dineroDisponible <= 0) {
                    System.out.println("ERROR! No seas bobi " + nombreJugador + ", si no apostas no jugas. Por favor, ingresa un monto mayor a $0");
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR! Por favor " + nombreJugador + ", ingresa un número válido.");
                dineroDisponible = -1;
            }
        } while (dineroDisponible > 50000 || dineroDisponible <= 0);

        System.out.println("Dinero aceptado: $" + dineroDisponible);
        System.out.println("-------------------------");


        // ELEGIR CANTIDAD DE MAZOS (DESCOMENTADO)
        boolean cantidadMazosValida = false;
        do {
            System.out.print(nombreJugador + ", con cuántos mazos querés jugar? (Entre 1 y 8): ");
            String mazosIngresados = entrada.next();
            try {
                cantidadMazos = Integer.parseInt(mazosIngresados);
                if (cantidadMazos < 1 || cantidadMazos > 8) {
                    System.out.println("Dale, elegí una cantidad de mazos entre 1 y 8, " + nombreJugador + ".");
                    cantidadMazosValida = false;
                } else {
                    cantidadMazosValida = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR! Eso no es un número válido. Dale, probá de nuevo.");
                cantidadMazosValida = false;
                cantidadMazos = 0;
            }
        } while (!cantidadMazosValida);


        // Consumir el salto de línea pendiente después de nextInt()
        entrada.nextLine();


        // Inicializar el "mezclador" (el conjunto de mazos)
        mezclador = new int[52 * cantidadMazos];
        int contadorCartas = 0;
        for (int m = 0; m < cantidadMazos; m++) { // Para cada mazo
            for (int p = 0; p < 4; p++) { // Para cada palo (aunque no lo mostremos, lo consideramos en la creación)
                for (int c = 1; c <= 13; c++) { // Para cada valor de carta (AS, 2-10, J, Q, K)
                    mezclador[contadorCartas++] = c;
                }
            }
        }
        // --- INICIO HARDCODEO DE CARTAS PARA DEMO (COMENTADO) ---
        // COMENTAR LA LÍNEA DE ABAJO PARA HARDCODEAR LAS CARTAS INICIALES
        mezclarMazo(mezclador, numeroRandom); // Original: Barajamos el mazo inicial (DESCOMENTADO)

        // // HARDCODEO DE CARTAS PARA DEMO:
        // // Asegúrate de que el 'mezclador' tenga al menos 10 posiciones para esta secuencia
        // if (mezclador.length >= 10) {
        //     // Ronda 1: Jugador con Blackjack (gana triple)
        //     mezclador[0] = 1;  // Jugador 1 (AS)
        //     mezclador[1] = 10; // Jugador 2 (10/J/Q/K)
        //     mezclador[2] = 5;  // Banca 1 (mostrada)
        //     mezclador[3] = 7;  // Banca 2 (oculta) - No se usarán en esta ronda si el jugador gana con BJ
        //
        //     // Ronda 2: Jugador gana con 21, Banca se pasa (gana doble)
        //     mezclador[4] = 8;  // Jugador 1
        //     mezclador[5] = 8;  // Jugador 2 (Total 16)
        //     mezclador[6] = 10; // Banca 1 (mostrada)
        //     mezclador[7] = 5;  // Banca 2 (oculta, Total 15)
        //     mezclador[8] = 5;  // Jugador pide (Total 21)
        //     mezclador[9] = 8;  // Banca pide (Total 23 - se pasa)
        //
        // } else {
        //     System.out.println("ADVERTENCIA: El mezclador es demasiado pequeño para la secuencia hardcodeada de demo. Se usará mezcla aleatoria.");
        //     mezclarMazo(mezclador, numeroRandom);
        // }
        // --- FIN HARDCODEO DE CARTAS PARA DEMO (COMENTADO) ---

        indiceActualMezclador = 0; // La primera carta a sacar es la del inicio del array

        // Bucle principal del juego (cada iteración es una ronda)
        do {
            // Reinicio de variables para cada ronda
            jugadorSePaso = false;
            jugadoBlackjackServido = false;
            puntajeBanca = 0;
            puntajeJugador = 0;
            apuestaValida = false; // Reiniciar para cada apuesta

            // Verificar si el mazo necesita ser mezclado de nuevo
            if (mezclador.length - indiceActualMezclador < minimoCartasAMezclar) {
                System.out.println("¡Che, el mezclador está casi vacío! Mezclando todas las cartas de nuevo, bo.");
                mezclarMazo(mezclador, numeroRandom);
                indiceActualMezclador = 0; // Reiniciamos el índice del mazo
            }

            // --- INICIO HARDCODEO DE APUESTA PARA DEMO (COMENTADO) ---
            System.out.println("Dinero disponible: $" + dineroDisponible);
            System.out.print(nombreJugador + " cuanto queres apostar? $");
            String apuestaIngresada = entrada.next();
            try {
                apuestaActual = Double.parseDouble(apuestaIngresada);
                if (apuestaActual > dineroDisponible || apuestaActual <= 0) {
                    System.out.println("ERROR! Tu apuesta es mayor a tu dinero disponible o es invalida. Intenta de nuevo " + nombreJugador + ".");
                } else {
                    apuestaValida = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR! Por favor " + nombreJugador + ", ingresa un número válido para tu apuesta.");
                apuestaActual = -1;
            }
            // } while (!apuestaValida); // DESCOMENTADO
            // apuestaActual = 100.0; // Hardcodeo la apuesta para el demo (COMENTADO)
            // System.out.println("Apuesta hardcodeada para demo: $" + apuestaActual); // (COMENTADO)
            // --- FIN HARDCODEO DE APUESTA PARA DEMO (COMENTADO) ---

            // Se mantiene el bucle de validación de apuesta para el juego interactivo
            while (!apuestaValida) {
                System.out.println("Dinero disponible: $" + dineroDisponible);
                System.out.print(nombreJugador + " cuanto queres apostar? $");
                apuestaIngresada = entrada.next();
                try {
                    apuestaActual = Double.parseDouble(apuestaIngresada);
                    if (apuestaActual > dineroDisponible || apuestaActual <= 0) {
                        System.out.println("ERROR! Tu apuesta es mayor a tu dinero disponible o es invalida. Intenta de nuevo " + nombreJugador + ".");
                    } else {
                        apuestaValida = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ERROR! Por favor " + nombreJugador + ", ingresa un número válido para tu apuesta.");
                    apuestaActual = -1;
                }
            }


            dineroDisponible = dineroDisponible - apuestaActual;
            System.out.println("Apuesta exitosa. Dinero disponible: $" + dineroDisponible);
            System.out.println("-------------------------");

            // TURNO DEL JUGADOR
            System.out.println(nombreJugador + " te toca!");

            // Repartir las primeras 2 cartas al jugador
            cartaActual = mezclador[indiceActualMezclador++]; // Sacamos la carta del mazo
            System.out.println(nombreJugador + " tu primera carta: " + nombreCarta(cartaActual));
            puntajeJugador = calcularValorCarta(cartaActual, puntajeJugador);

            cartaActual = mezclador[indiceActualMezclador++]; // Sacamos la segunda carta
            System.out.println(nombreJugador + " tu segunda carta: " + nombreCarta(cartaActual));
            puntajeJugador = puntajeJugador + calcularValorCarta(cartaActual, puntajeJugador);

            System.out.println(nombreJugador + " tu puntaje es: " + puntajeJugador);

            // Repartir 1 carta visible a la banca
            int cartaBancaMostrada = mezclador[indiceActualMezclador++];
            puntajeBanca = puntajeBanca + calcularValorCarta(cartaBancaMostrada, puntajeBanca);
            System.out.println("La banca muestra una carta: " + nombreCarta(cartaBancaMostrada));


            // Lógica inicial de Blackjack (antes de que el jugador pida más cartas)
            if (puntajeJugador == 21) {
                System.out.println("¡BLACKJACK! ¡Felicitaciones " + nombreJugador + "!");
                dineroDisponible = dineroDisponible + (apuestaActual * 3); // Se paga triple
                jugadoBlackjackServido = true;
            } else if (puntajeJugador > 21) {
                System.out.println("¡Uff! Te pasaste de 21 con tus primeras cartas. ¡PERDISTE esta ronda " + nombreJugador + "!");
                jugadorSePaso = true;
            } else {
                // Si el jugador no se pasó ni sacó Blackjack, pregunta si quiere más cartas
                do {
                    // --- HARDCODEO DE RESPUESTAS PARA DEMO (COMENTADO) ---
                    // // Para la primera ronda (Blackjack), el jugador no pide.
                    // // Para la segunda ronda (gana por bust), el jugador pide una vez.
                    // if (puntajeJugador == 16 && indiceActualMezclador == 9) {
                    //     respuestaUsuario = "S";
                    //     System.out.println("Respuesta hardcodeada para demo: Queres otra carta? (S/N): S");
                    // } else {
                        System.out.print("Queres otra carta? (S/N): ");
                        respuestaUsuario = entrada.next();
                    // }
                    // --- FIN HARDCODEO DE RESPUESTAS (COMENTADO) ---

                    // Validar la respuesta del usuario (Mantiene la lógica original)
                    while (!respuestaUsuario.equalsIgnoreCase("s") && !respuestaUsuario.equalsIgnoreCase("n")) {
                        System.out.println("Respuesta inválida. Por favor, ingresa 'S' para sí o 'N' para no.");
                        System.out.print("Queres otra carta? (S/N): ");
                        respuestaUsuario = entrada.next();
                    }

                    if (respuestaUsuario.equalsIgnoreCase("s")) {
                        cartaActual = mezclador[indiceActualMezclador++]; // Sacamos otra carta
                        System.out.println(nombreJugador + " tu nueva carta: " + nombreCarta(cartaActual));
                        puntajeJugador = puntajeJugador + calcularValorCarta(cartaActual, puntajeJugador);
                        System.out.println(nombreJugador + " tu puntaje actual: " + puntajeJugador);

                        // Si el jugador se pasa al pedir carta
                        if (puntajeJugador > 21) {
                            System.out.println("¡Uff! Te pasaste de 21. ¡PERDISTE esta ronda " + nombreJugador + "!");
                            jugadorSePaso = true;
                            break; // Sale del bucle de pedir cartas
                        } else if (puntajeJugador == 21) {
                            System.out.println("¡BlackJack! ¡Llegaste a 21! Te plantas automáticamente.");
                            break; // Sale del bucle de pedir cartas
                        }
                    }
                } while (respuestaUsuario.equalsIgnoreCase("s") && puntajeJugador < 21);
            }

            // Mostrar el puntaje final del jugador
            System.out.println(nombreJugador + " tu puntaje final: " + puntajeJugador);
            System.out.println("-------------------------");

            // TURNO DE LA BANCA (solo si el jugador no se pasó y no sacó Blackjack servido)
            if (!jugadorSePaso && !jugadoBlackjackServido) {
                System.out.println("¡Le toca a la Banca!");

                // Revelar la segunda carta de la banca (la que estaba "oculta")
                int cartaBancaOculta = mezclador[indiceActualMezclador++];
                puntajeBanca = puntajeBanca + calcularValorCarta(cartaBancaOculta, puntajeBanca);
                System.out.println("La banca revela su segunda carta: " + nombreCarta(cartaBancaOculta));
                System.out.println("Puntaje actual de la banca: " + puntajeBanca);

                // La banca pide cartas hasta sumar 17 o más
                while (puntajeBanca < 17) {
                    cartaActual = mezclador[indiceActualMezclador++];
                    System.out.println("La banca pide carta: " + nombreCarta(cartaActual));
                    puntajeBanca = puntajeBanca + calcularValorCarta(cartaActual, puntajeBanca);
                    System.out.println("Puntaje de la banca: " + puntajeBanca);
                }
                System.out.println("Puntaje final de la banca: " + puntajeBanca);
                System.out.println("-------------------------");

            } else {
                if (jugadorSePaso) {
                    System.out.println("La banca no juega porque te pasaste.");
                } else if (jugadoBlackjackServido) {
                    System.out.println("La banca no juega porque ganaste con BlackJack.");
                }
            }

            // RESULTADOS DE LA RONDA
            if (jugadorSePaso) {
                System.out.println("RESULTADO: ¡PERDISTE! Te pasaste de 21.");
            } else if (jugadoBlackjackServido) {
                System.out.println("RESULTADO: ¡GANASTE con Blackjack servido!");
            } else if (puntajeBanca > 21) {
                System.out.println("RESULTADO: ¡GANASTE! La banca se paso de 21.");
                dineroDisponible = dineroDisponible + (apuestaActual * 2); // Se paga doble
            } else if (puntajeJugador > puntajeBanca) {
                System.out.println("RESULTADO: ¡GANASTE! Tenes mas puntos que la banca.");
                dineroDisponible = dineroDisponible + (apuestaActual * 2); // Se paga doble
            } else if (puntajeJugador == puntajeBanca) {
                System.out.println("RESULTADO: ¡EMPATE! Se te devuelve la apuesta.");
                dineroDisponible = dineroDisponible + apuestaActual; // Recupera la apuesta
            } else {
                System.out.println("RESULTADO: ¡PERDISTE! La banca tiene mas puntos.");
            }

            System.out.println(nombreJugador + " tu dinero disponible para seguir jugando es: $" + dineroDisponible);
            System.out.println("-------------------------");

            // PREGUNTAR SI QUIERE VOLVER A JUGAR O RECARGAR (Lógica condicional)
            boolean respuestaIngresadaValida = false; // Variable para controlar el bucle
            do {
                if (dineroDisponible > 0) {
                    System.out.print(nombreJugador + " queres jugar de nuevo? Si (S), No (N), Retirarte (X)?: ");
                } else {
                    // No tiene dinero para jugar, solo puede recargar o retirarse
                    System.out.println(nombreJugador + " no tenes dinero para seguir jugando.");
                    System.out.print("Queres Recargar dinero (R) o Retirarte (X)?: ");
                }
                respuestaUsuario = entrada.next();

                // Validar la respuesta según las opciones mostradas
                if (dineroDisponible > 0) {
                    if (respuestaUsuario.equalsIgnoreCase("s") || respuestaUsuario.equalsIgnoreCase("n") || respuestaUsuario.equalsIgnoreCase("x")) {
                        respuestaIngresadaValida = true; // La respuesta es válida, salir del bucle
                    } else {
                        System.out.println("Respuesta inválida. Por favor, ingresa 'S', 'N' o 'X'.");
                        respuestaIngresadaValida = false; // La respuesta no es válida, continuar el bucle
                    }
                } else { // dineroDisponible <= 0
                    if (respuestaUsuario.equalsIgnoreCase("r") || respuestaUsuario.equalsIgnoreCase("x")) {
                        respuestaIngresadaValida = true; // La respuesta es válida, salir del bucle
                    } else {
                        System.out.println("Respuesta inválida. Por favor, ingresa 'R' o 'X'.");
                        respuestaIngresadaValida = false; // La respuesta no es válida, continuar el bucle
                    }
                }
            } while (!respuestaIngresadaValida); // El bucle se repite hasta que la respuesta sea válida

            // Lógica de recarga de dinero
            if (respuestaUsuario.equalsIgnoreCase("r")) {
                double montoRecarga;
                boolean montoValido = false;
                do {
                    System.out.print(nombreJugador + ", cuánto dinero querés recargar (entre $1 y $50.000)? $");
                    String recargaIngresada = entrada.next();
                    try {
                        montoRecarga = Double.parseDouble(recargaIngresada);
                        if (montoRecarga > 50000 || montoRecarga <= 0) {
                            System.out.println("ERROR! El monto de recarga debe ser entre $1 y $50.000. Por favor, ingresa un monto válido.");
                        } else {
                            dineroDisponible += montoRecarga; // Suma el monto recargado
                            System.out.println("¡Recarga exitosa! Tu nuevo dinero disponible es: $" + dineroDisponible);
                            montoValido = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("ERROR! Por favor " + nombreJugador + ", ingresa un número válido para tu recarga.");
                        montoRecarga = -1; // Valor inválido para que el bucle se repita
                    }
                } while (!montoValido);
                respuestaUsuario = "s"; // Después de recargar, asumimos que quiere seguir jugando
            }

        } while (respuestaUsuario.equalsIgnoreCase("s")); // Repetir mientras el jugador quiera seguir o haya recargado

        // Mensaje final al terminar el juego
        System.out.println("-------------------------");
        if (respuestaUsuario.equalsIgnoreCase("x")) { // Si elige 'X' para retirarse
            System.out.println(nombreJugador + " te retiraste del juego. ¡Gracias por jugar!");
        } else { // Si elige 'N' o se queda sin dinero y no recarga
            System.out.println(nombreJugador + " gracias por jugar. ¡Hasta la próxima!");
        }
        System.out.println(nombreJugador + " te llevas: $" + dineroDisponible);
        entrada.close(); // Cerrar el Scanner para liberar recursos
    }
}
