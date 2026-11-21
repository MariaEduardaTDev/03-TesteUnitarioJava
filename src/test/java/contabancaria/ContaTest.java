package contabancaria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Testes unitários para a classe Conta.
 *
 * PARTE 1 — Testes de exemplo (Construtor) já estão prontos.
 * PARTE 2 — Testes escritos seguindo o ciclo TDD: Red -> Green -> Refactor.
 *
 * Padrão de nomes: metodo_Cenario_ResultadoEsperado
 * Padrão interno: Arrange (preparar) - Act (executar) - Assert (verificar)
 */
class ContaTest {

    private static final double TOLERANCIA = 0.001;

    // =======================================================
    // PARTE 1 — EXEMPLO GUIADO: Testes do Construtor
    // Observe o padrão Arrange-Act-Assert (AAA)
    // =======================================================

    @Test
    void construtor_DadosValidos_CriaContaCorretamente() {
        // Arrange & Act
        var conta = new Conta("Maria", 100);

        // Assert
        assertEquals("Maria", conta.getTitular());
        assertEquals(100, conta.getSaldo());
        assertTrue(conta.isAtiva());
    }

    @Test
    void construtor_SemSaldoInicial_CriaContaComSaldoZero() {
        // Arrange & Act
        var conta = new Conta("João");

        // Assert
        assertEquals("João", conta.getTitular());
        assertEquals(0, conta.getSaldo());
        assertTrue(conta.isAtiva());
    }

    @Test
    void construtor_TitularNulo_LancaIllegalArgumentException() {
        // Assert — verifica que a exceção é lançada
        assertThrows(IllegalArgumentException.class, () -> new Conta(null));
    }

    @Test
    void construtor_TitularVazio_LancaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Conta(""));
    }

    @Test
    void construtor_SaldoNegativo_LancaIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Conta("Maria", -50));
    }

    @ParameterizedTest
    @CsvSource({
            "Ana,    0",
            "Carlos, 1000",
            "Beatriz, 0.01"
    })
    void construtor_VariosValoresValidos_CriaContaCorretamente(String titular, double saldo) {
        // Act
        var conta = new Conta(titular, saldo);

        // Assert
        assertEquals(titular, conta.getTitular());
        assertEquals(saldo, conta.getSaldo(), 0.001);
        assertTrue(conta.isAtiva());
    }

    // =======================================================
    // PARTE 2 — TESTES ESCRITOS NO CICLO TDD
    // =======================================================

    /**
     * Helper: cria uma conta já encerrada (saldo zero é pré-requisito para
     * encerrar).
     */
    private Conta contaEncerrada(String titular) {
        var conta = new Conta(titular);
        conta.encerrar();
        return conta;
    }

    // ----------------------- depositar -----------------------

    @Test
    void depositar_ValorValido_AtualizaSaldo() {
        // Arrange
        var conta = new Conta("Maria", 100);

        // Act
        conta.depositar(50);

        // Assert
        assertEquals(150, conta.getSaldo(), TOLERANCIA);
    }

    @Test
    void depositar_VariosDepositos_AcumulaSaldo() {
        // Arrange
        var conta = new Conta("Maria", 0);

        // Act
        conta.depositar(10);
        conta.depositar(0.50);

        // Assert
        assertEquals(10.50, conta.getSaldo(), TOLERANCIA);
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0, -0.01, -100 })
    void depositar_ValorZeroOuNegativo_LancaIllegalArgumentException(double valor) {
        // Arrange
        var conta = new Conta("Maria", 100);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(valor));
    }

    @Test
    void depositar_ValorInvalido_NaoAlteraSaldo() {
        // Arrange
        var conta = new Conta("Maria", 100);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(-20));
        assertEquals(100, conta.getSaldo(), TOLERANCIA);
    }

    @Test
    void depositar_ContaEncerrada_LancaIllegalStateException() {
        // Arrange
        var conta = contaEncerrada("Maria");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.depositar(50));
    }

    // ------------------------- sacar -------------------------

    @Test
    void sacar_ValorValido_AtualizaSaldo() {
        // Arrange
        var conta = new Conta("Maria", 100);

        // Act
        conta.sacar(30);

        // Assert
        assertEquals(70, conta.getSaldo(), TOLERANCIA);
    }

    @Test
    void sacar_ValorIgualAoSaldo_DeixaSaldoZero() {
        // Arrange — caso de borda: saque do saldo inteiro é permitido
        var conta = new Conta("Maria", 100);

        // Act
        conta.sacar(100);

        // Assert
        assertEquals(0, conta.getSaldo(), TOLERANCIA);
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0, -0.01, -100 })
    void sacar_ValorZeroOuNegativo_LancaIllegalArgumentException(double valor) {
        // Arrange
        var conta = new Conta("Maria", 100);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(valor));
    }

    @Test
    void sacar_ValorAcimaDoSaldo_LancaIllegalStateException() {
        // Arrange
        var conta = new Conta("Maria", 100);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.sacar(100.01));
    }

    @Test
    void sacar_ValorAcimaDoSaldo_NaoAlteraSaldo() {
        // Arrange
        var conta = new Conta("Maria", 100);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.sacar(500));
        assertEquals(100, conta.getSaldo(), TOLERANCIA);
    }

    @Test
    void sacar_ContaEncerrada_LancaIllegalStateException() {
        // Arrange
        var conta = contaEncerrada("Maria");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.sacar(10));
    }

    // ------------------------ encerrar ------------------------

    @Test
    void encerrar_SaldoZero_DesativaConta() {
        // Arrange
        var conta = new Conta("Maria", 0);

        // Act
        conta.encerrar();

        // Assert
        assertFalse(conta.isAtiva());
    }

    @Test
    void encerrar_SaldoZeradoPorSaque_DesativaConta() {
        // Arrange
        var conta = new Conta("Maria", 100);
        conta.sacar(100);

        // Act
        conta.encerrar();

        // Assert
        assertFalse(conta.isAtiva());
    }

    @Test
    void encerrar_ComSaldoPositivo_LancaIllegalStateException() {
        // Arrange
        var conta = new Conta("Maria", 0.01);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.encerrar());
    }

    @Test
    void encerrar_ComSaldoPositivo_MantemContaAtiva() {
        // Arrange
        var conta = new Conta("Maria", 100);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.encerrar());
        assertTrue(conta.isAtiva());
    }

    @Test
    void encerrar_ContaJaInativa_LancaIllegalStateException() {
        // Arrange
        var conta = contaEncerrada("Maria");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> conta.encerrar());
    }

    // ----------------------- transferir -----------------------

    @Test
    void transferir_ValorValido_AtualizaSaldoDasDuasContas() {
        // Arrange
        var origem = new Conta("Maria", 100);
        var destino = new Conta("João", 20);

        // Act
        origem.transferir(destino, 30);

        // Assert
        assertEquals(70, origem.getSaldo(), TOLERANCIA);
        assertEquals(50, destino.getSaldo(), TOLERANCIA);
    }

    @Test
    void transferir_SaldoInteiro_DeixaOrigemComSaldoZero() {
        // Arrange — caso de borda: transferir todo o saldo é permitido
        var origem = new Conta("Maria", 100);
        var destino = new Conta("João", 0);

        // Act
        origem.transferir(destino, 100);

        // Assert
        assertEquals(0, origem.getSaldo(), TOLERANCIA);
        assertEquals(100, destino.getSaldo(), TOLERANCIA);
    }

    @ParameterizedTest
    @ValueSource(doubles = { 0, -0.01, -100 })
    void transferir_ValorZeroOuNegativo_LancaIllegalArgumentException(double valor) {
        // Arrange
        var origem = new Conta("Maria", 100);
        var destino = new Conta("João", 20);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> origem.transferir(destino, valor));
    }

    @Test
    void transferir_ValorInvalido_NaoAlteraSaldos() {
        // Arrange
        var origem = new Conta("Maria", 100);
        var destino = new Conta("João", 20);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> origem.transferir(destino, -10));
        assertEquals(100, origem.getSaldo(), TOLERANCIA);
        assertEquals(20, destino.getSaldo(), TOLERANCIA);
    }

    @Test
    void transferir_ValorAcimaDoSaldo_LancaIllegalStateException() {
        // Arrange
        var origem = new Conta("Maria", 100);
        var destino = new Conta("João", 20);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> origem.transferir(destino, 100.01));
    }

    @Test
    void transferir_ValorAcimaDoSaldo_NaoAlteraSaldos() {
        // Arrange
        var origem = new Conta("Maria", 100);
        var destino = new Conta("João", 20);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> origem.transferir(destino, 500));
        assertEquals(100, origem.getSaldo(), TOLERANCIA);
        assertEquals(20, destino.getSaldo(), TOLERANCIA);
    }

    @Test
    void transferir_ContaOrigemEncerrada_LancaIllegalStateException() {
        // Arrange
        var origem = contaEncerrada("Maria");
        var destino = new Conta("João", 20);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> origem.transferir(destino, 10));
    }

    @Test
    void transferir_ContaDestinoEncerrada_LancaIllegalStateException() {
        // Arrange
        var origem = new Conta("Maria", 100);
        var destino = contaEncerrada("João");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> origem.transferir(destino, 10));
    }

    @Test
    void transferir_ContaDestinoEncerrada_NaoAlteraSaldos() {
        // Arrange
        var origem = new Conta("Maria", 100);
        var destino = contaEncerrada("João");

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> origem.transferir(destino, 10));
        assertEquals(100, origem.getSaldo(), TOLERANCIA);
        assertEquals(0, destino.getSaldo(), TOLERANCIA);
    }
}