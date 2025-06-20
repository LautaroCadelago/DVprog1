package com.cadelago.parcial2lautarocadelago;

import java.util.Scanner;
import java.util.Random;

public class Parcial2LautaroCadelago {

    // CALCULAR VALOR DE LA CARTA
    public static int calcularValorCarta(int numeroCarta, int puntajeActual) {
        switch (numeroCarta) {
            case 1: // AS
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

    // MEZCLAR MAZO
    public static void mezclarMazo(int[] arrayCartas, Random mezclaRandom) {
        
        int cantidadCartas = arrayCartas.length;
        
        for (int i = cantidadCartas; i > 1; i--) {
            
            int posicionRandom = mezclaRandom.nextInt(i); 
            
            int cartaTemporal = arrayCartas[i - 1];
            
            arrayCartas[i - 1] = arrayCartas[posicionRandom];
            
            arrayCartas[posicionRandom] = cartaTemporal;
        }
    }

    // NUMERO DE CARTA
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
        
        // PUNTAJE
        int puntajeJugador;         // Puntos del jugador
        int puntajeBanca;           // Puntos de la banca
        
        // JUEGO
        int cartaActual;            // Carta sacada
        boolean jugadorSePaso;
        boolean jugadoBlackjackServido;
        
        // DINERO
        double dineroDisponible;    // Dinero para apostar
        double apuestaActual;       // Dinero apostado
        boolean apuestaValida;
        
        // RESPUESTAS
        String respuestaJugador;    // S/N/R/X
        boolean respuestaIngresadaValida;

        // MEZCLADOR
        int cantidadMazos = 0;
        int[] mezclador;            // Cuantos mazos
        int indiceActualMezclador;  // Que carta sigue en el mazo a mezclar
        int minimoCartasAMezclar = 52;
        int contadorCartas = 0;
        boolean cantidadMazosValida = false;
        
        // RECARGA
        double montoRecarga;
        boolean montoValido;
        
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

        // PEDIR NOMBRE
        do {
            System.out.println("Ingresa tu nombre:");
            
            nombreJugador = entrada.nextLine();
            
            if (nombreJugador.isEmpty()) {
                
                System.out.println("ERROR! El nombre no puede estar vacío. Por favor, ingresa un nombre válido.");
            }
        } while (nombreJugador.isEmpty());

        // INGRESAR DINERO INICIAL Y VALIDAR
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

        // ELEGIR CANTIDAD DE MAZOS
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

        entrada.nextLine();

        // MEZCLAR
        mezclador = new int[52 * cantidadMazos];
        
        for (int m = 0; m < cantidadMazos; m++) {  // Mazos
            
            for (int p = 0; p < 4; p++) {          // LAPalos
                
                for (int c = 1; c <= 13; c++) {    // Numero de carta
                    
                    mezclador[contadorCartas++] = c;
                }
            }
        }
        
        mezclarMazo(mezclador, numeroRandom);

        indiceActualMezclador = 0;

        // RONDA
        do { 
            // Reinicio de variables para cada ronda
            puntajeJugador = 0;
            puntajeBanca = 0;
            jugadorSePaso = false;
            jugadoBlackjackServido = false; 
            apuestaValida = false;
            respuestaJugador = "";
            respuestaIngresadaValida = false;

            // Verificar si hay que mezclar
            if (mezclador.length - indiceActualMezclador < minimoCartasAMezclar) { 
                
                System.out.println("Quedan pocas cartas en el mazo! Mezclando todas las cartas de nuevo");
                
                mezclarMazo(mezclador, numeroRandom); 
                
                indiceActualMezclador = 0;
            }

            // APOSTAR
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
         
            // VALIDAR APUESTA
            while (!apuestaValida) {
                
                System.out.println("Dinero disponible: $" + dineroDisponible);
                System.out.print(nombreJugador + " cuanto queres apostar? $");
                
                apuestaIngresada = entrada.next(); // Re-uso de la variable ya declarada
                
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

            // Repartir 1er carta al jugador
            cartaActual = mezclador[indiceActualMezclador++];
            
            System.out.println(nombreJugador + " tu primera carta: " + nombreCarta(cartaActual));
            
            puntajeJugador = calcularValorCarta(cartaActual, puntajeJugador);
            
            // Repartir 2da carta al jugador
            cartaActual = mezclador[indiceActualMezclador++];
            
            System.out.println(nombreJugador + " tu segunda carta: " + nombreCarta(cartaActual));
            
            puntajeJugador = puntajeJugador + calcularValorCarta(cartaActual, puntajeJugador);

            System.out.println(nombreJugador + " tu puntaje es: " + puntajeJugador);

            // Repartir 1er carta a la banca
            int cartaBancaMostrada = mezclador[indiceActualMezclador++];
            
            puntajeBanca = puntajeBanca + calcularValorCarta(cartaBancaMostrada, puntajeBanca);
            
            System.out.println("La banca muestra una carta: " + nombreCarta(cartaBancaMostrada));

            // BLACKJACK SERVIDO
            if (puntajeJugador == 21) {
                
                System.out.println("¡BLACKJACK! ¡Felicitaciones " + nombreJugador + "!");
                
                dineroDisponible = dineroDisponible + (apuestaActual * 3); // Se paga triple
                
                jugadoBlackjackServido = true;
                
            // JUGADOR SE PASO   
            } else if (puntajeJugador > 21) {
                
                System.out.println("¡Uff! Te pasaste de 21 con tus primeras cartas. ¡PERDISTE esta ronda " + nombreJugador + "!");
                
                jugadorSePaso = true;
                
            // PEDIR CARTA
            } else {
                
                do {
                    System.out.print("Queres otra carta? (S/N): ");
                        
                    respuestaJugador = entrada.next();
                    
                // Validar la respuesta del jugador
                while (!respuestaJugador.equalsIgnoreCase("s") && !respuestaJugador.equalsIgnoreCase("n")) {
                        
                        System.out.println("Respuesta inválida. Por favor, ingresa 'S' para sí o 'N' para no.");
                        System.out.print("Queres otra carta? (S/N): ");
                        
                        respuestaJugador = entrada.next();
                    }

                    if (respuestaJugador.equalsIgnoreCase("s")) {
                        
                        cartaActual = mezclador[indiceActualMezclador++];
                        
                        System.out.println(nombreJugador + " tu nueva carta: " + nombreCarta(cartaActual));
                        
                        puntajeJugador = puntajeJugador + calcularValorCarta(cartaActual, puntajeJugador);
                        
                        System.out.println(nombreJugador + " tu puntaje actual: " + puntajeJugador);

                        // Si pide y se pasa
                        if (puntajeJugador > 21) {
                            
                            System.out.println("¡Uff! Te pasaste de 21. ¡PERDISTE esta ronda " + nombreJugador + "!");
                            
                            jugadorSePaso = true;
                            
                            break;
                            
                        // Si pide y llega a 21
                        } else if (puntajeJugador == 21) {
                            
                            System.out.println("¡BlackJack! ¡Llegaste a 21! Te plantas automáticamente.");
                            
                            break;
                        }
                    }
                } while (respuestaJugador.equalsIgnoreCase("s") && puntajeJugador < 21);
            }

            System.out.println(nombreJugador + " tu puntaje final: " + puntajeJugador);
            System.out.println("-------------------------");

            // TURNO DE LA BANCA
            if (!jugadorSePaso && !jugadoBlackjackServido) { 
                System.out.println("¡Le toca a la Banca!");

                // Repartir 2da carta a la banca
                int cartaBancaOculta = mezclador[indiceActualMezclador++];
                
                puntajeBanca = puntajeBanca + calcularValorCarta(cartaBancaOculta, puntajeBanca);
                
                System.out.println("La banca revela su segunda carta: " + nombreCarta(cartaBancaOculta)); 
                System.out.println("Puntaje actual de la banca: " + puntajeBanca);

                // La banca pide carta
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

            // RESULTADOS DE RONDA
            if (jugadorSePaso) {
                System.out.println("RESULTADO: ¡PERDISTE! Te pasaste de 21.");
                
            } else if (jugadoBlackjackServido) { 
                System.out.println("RESULTADO: ¡GANASTE con Blackjack servido!"); // Paga triple
                
            } else if (puntajeBanca > 21) {
                System.out.println("RESULTADO: ¡GANASTE! La banca se paso de 21.");
                
                dineroDisponible = dineroDisponible + (apuestaActual * 2); // Paga doble
                
            } else if (puntajeJugador > puntajeBanca) {
                System.out.println("RESULTADO: ¡GANASTE! Tenes mas puntos que la banca.");
                
                dineroDisponible = dineroDisponible + (apuestaActual * 2); // Paga doble
                
            } else if (puntajeJugador == puntajeBanca) {
                System.out.println("RESULTADO: ¡EMPATE! Se te devuelve la apuesta.");
                
                dineroDisponible = dineroDisponible + apuestaActual; // Recupera apuesta
                
            } else {
                System.out.println("RESULTADO: ¡PERDISTE! La banca tiene mas puntos.");
            }

            System.out.println(nombreJugador + " tu dinero disponible para seguir jugando es: $" + dineroDisponible);
            System.out.println("-------------------------");

            // VOLVER A JUGAR O RECARGAR
            do {
                if (dineroDisponible > 0) {
                    System.out.print(nombreJugador + " queres jugar de nuevo? Si (S), No (N), Retirarte (X)?: ");
                    
                } else {
                    // No tiene dinero, recargar o retirarse
                    System.out.println(nombreJugador + " no tenes dinero para seguir jugando.");
                    System.out.print("Queres Recargar dinero (R) o Retirarte (X)?: ");
                }
                respuestaJugador = entrada.next();

                // Tiene dinero 
                if (dineroDisponible > 0) {
                    if (respuestaJugador.equalsIgnoreCase("s") || respuestaJugador.equalsIgnoreCase("n") || respuestaJugador.equalsIgnoreCase("x")) {
                        
                        respuestaIngresadaValida = true;
                        
                    } else {
                        System.out.println("Respuesta inválida. Por favor, ingresa 'S', 'N' o 'X'.");
                        
                        respuestaIngresadaValida = false;
                    }
                    
                 // No tiene dinero   
                } else {
                    if (respuestaJugador.equalsIgnoreCase("r") || respuestaJugador.equalsIgnoreCase("x")) {
                        
                        respuestaIngresadaValida = true;
                        
                    } else {
                        System.out.println("Respuesta inválida. Por favor, ingresa 'R' o 'X'.");
                        
                        respuestaIngresadaValida = false;
                    }
                }
            } while (!respuestaIngresadaValida);

            // RECARGA
            if (respuestaJugador.equalsIgnoreCase("r")) {
                
                montoValido = false;
                
                do {
                    System.out.print(nombreJugador + ", cuánto dinero querés recargar (entre $1 y $50.000)? $");
                    
                    String recargaIngresada = entrada.next();
                    
                    try {
                        montoRecarga = Double.parseDouble(recargaIngresada);
                        
                        if (montoRecarga > 50000 || montoRecarga <= 0) {
                            
                            System.out.println("ERROR! El monto de recarga debe ser entre $1 y $50.000. Por favor, ingresa un monto válido.");
                            
                        } else {
                            dineroDisponible += montoRecarga;
                            
                            System.out.println("¡Recarga exitosa! Tu nuevo dinero disponible es: $" + dineroDisponible);
                            
                            montoValido = true;
                        }
                    } catch (NumberFormatException e) {
                        
                        System.out.println("ERROR! Por favor " + nombreJugador + ", ingresa un número válido para tu recarga.");
                        
                        montoRecarga = -1;
                    }
                } while (!montoValido);
                
                respuestaJugador = "s";
            }

        } while (respuestaJugador.equalsIgnoreCase("s"));

        // FIN DEL JUEGO
        System.out.println("-------------------------");
        
        if (respuestaJugador.equalsIgnoreCase("x")) {
            System.out.println(nombreJugador + " te retiraste del juego. ¡Gracias por jugar!");
        
        } else {
            System.out.println(nombreJugador + " gracias por jugar. ¡Hasta la próxima!");
        }
        
        System.out.println(nombreJugador + " te llevas: $" + dineroDisponible);
        
        entrada.close();
    }
}
