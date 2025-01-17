# Practicas SAD

## Practica 1

- La primera pràctica demana l’implementació del protocol de BufferedReader, afegint la capacitat de poder treballar amb seqüències d’escapament per implementar funcionalitats com pot ser la capacitat de esborrar caràcters, moure el cursor, i la possibilitat de fer el mateix amb el ratolí.

- Aquesta pràctica comptava amb 4 parts opcionals clarament marcades:

| **Part Opcional**                       | **Implementació** | 
|:---------------------------------------:|:---------:|
| Suport bàsic de ratolí                  | ✅       |
| Suport bàsic de formulari               | ❌       |
| Editor bàsic de consola                 | ❌       |
| PropertyChangeListener/Support          | ❌       |

### Implementació de suport bàsic de ratolí

  Per a poder implementar suport per a ratolí, calia fer pocs canvis en el sistema. El canvi important és fer una crida a la sequència de caràcters que activa que el ratolí imprimeixi la seva posició i estat relativa a la posició de la pantalla
  mitjançant aquest codi a [Console.java](src/EditableBufferedReader/Console.java)
  ```java
  public Console(Line l) {
        this.linia = l;
        System.out.print(EscapeSeq.ENABLE_MOUSE); //activar lectura ratolí
        System.out.print(EscapeSeq.ENABLE_MOUSE_EX); //activar lectura ratolí extensa (click: ^[[<XXX;YYY;ZZZm )
    }
  ```
Un cop activat això cal establir en [EditableBufferedReader.java](src/EditableBufferedReader/EditableBufferedReader.java) quina funció realitzarà el ratolí en un click.
```java
    case(-EscapeSeq.MOUSE)://(click: ^[[<XXX;YYY;ZZZm)

      String strT = "";
      char charM = (char) this.read();

      while(charM != ';'){
        strT += charM;
        charM = (char) this.read();
      }  

      String strX = "";
      charM = (char) this.read();
      while(charM != ';'){
        strX += charM;
        charM = (char) this.read();
      }
      
      @SuppressWarnings("unused") 
      String strY = "";
      charM = (char) this.read();
      while(charM != 'm' && charM != 'M'){
        strY += charM;
        charM = (char) this.read();
      }
      if(Integer.parseInt(strT) != 0 || charM == 'm'){
        break;
      }

      linia.move(-Integer.parseInt(strX));
      
      break;
```
Tenint aquest codi complert, cal acabar desactivant la lectura del ratolí abans de tancar el codi. 
```java
    System.out.print(EscapeSeq.DISABLE_MOUSE); //activar lectura ratolí
    System.out.print(EscapeSeq.DISABLE_MOUSE_EX); //activar lectura ratolí extensa (click: ^[[<XXX;YYY;ZZZm )
```

> [!NOTE]
> La versió sense MVC no es troba disponible en la versió actual del projecte, doncs vam reescriure el EditableBufferedReader per fer ús de StringBuilder() en comptes d'un Array de caràcters.

## Pràctica 2

La pràctica 2 consisteix en l’implementació d’un sistema client servidor per poder construïr a sobre una aplicació de terminal de xat, permetent la comunicació entre dos clients. En aquesta part del projecte, vam decidir fer servir la classe ConcurrentHashMap per solventar els problemes relacionats amb l’accés concurrent a les dades del servidor.

```java
  public UsersHashMap() {
        userMap = new ConcurrentHashMap<>();
  }
```


> [!IMPORTANT]
> Com que el nostre projecte està encarat també a la creació d'un xat, vam aprofitar el servidor i client creat en aquesta pràctica.
> A més a més, com a pas intermig per desenvolupar el client gràfic en python, vam desenvolupar també una versió en python del client de terminal
> que es demana en aquesta pràctica
