# PlayerVaultZ-Drake

Parche privado de producción para PlayerVaultZ 1.1.0 en DrakesCraft.

## Motivo

Quick Pick retiraba objetos desde `VaultPage#getItems()`, que devuelve una copia del arreglo. El jugador recibía el objeto, pero el slot persistente nunca cambiaba y permitía duplicarlo.

Este parche:

- muta el slot real mediante `VaultPage#setItem`;
- conserva todos los metadatos de objetos Slimefun;
- compara objetos completos, no solo su material vanilla;
- calcula espacio real antes de retirar;
- bloquea retiradas superpuestas mientras se persiste el vault;
- incluye pruebas de regresión para retiros totales y parciales.

## Compilación

El binario original no se versiona porque no se encontró licencia pública del proyecto. Colocar `PlayerVaultZ-1.1.0.jar` en `vendor/` y validar:

```text
SHA-256: F7225476AF8551898FA468112465C56C941E15185377B9690D642D7E28000CFB
```

Luego ejecutar `mvn clean package`. El JAR final queda en `target/PlayerVaultZ-1.1.0-Drake.jar`.
