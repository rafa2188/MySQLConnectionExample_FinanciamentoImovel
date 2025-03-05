import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class MySQLConnectionExample_FinanciamentoImovel {

    public static void main(String[] args) {
        // Dados de conexão
        String url = "jdbc:mysql://localhost:3306/";  // URL sem o nome do banco de dados
        String user = "Rafa";  // Substitua pelo seu usuário do MySQL
        String password = "2188";  // Substitua pela sua senha do MySQL
        String dbName = "simulacao_Financiamento";  // Nome do banco de dados

        Connection conn = null;
        Statement stmt = null;

        try {
            // Registrar o driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Abrir uma conexão com o MySQL (sem especificar o banco de dados)
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();

            // Verificar se o banco de dados já existe e criá-lo, se necessário
            String checkDbSql = "CREATE DATABASE IF NOT EXISTS " + dbName;
            stmt.executeUpdate(checkDbSql);
            System.out.println("Banco de dados '" + dbName + "' verificado ou criado com sucesso!");

            // Fechar a conexão atual para reconectar ao banco de dados específico
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();

            // Conectar ao banco de dados específico
            conn = DriverManager.getConnection(url + dbName, user, password);
            stmt = conn.createStatement();

            // SQL para criar a tabela de financiamento de imóvel
            String createTableSql = "CREATE TABLE IF NOT EXISTS financiamento_imovel (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome_cliente VARCHAR(255) NOT NULL, " +
                    "valor_imovel DECIMAL(15, 2) NOT NULL, " +
                    "valor_entrada DECIMAL(15, 2) NOT NULL, " +
                    "prazo_financiamento INT NOT NULL, " +
                    "taxa_juros DECIMAL(5, 2) NOT NULL, " +
                    "valor_parcela DECIMAL(15, 2) NOT NULL, " +
                    "data_simulacao DATE NOT NULL)";
            stmt.executeUpdate(createTableSql);
            System.out.println("Tabela 'financiamento_imovel' criada com sucesso!");

            // Inserir uma simulação de financiamento
            String nomeCliente = "Rafael Garcia";
            double valorImovel = 500000.00;
            double valorEntrada = 100000.00;
            int prazoFinanciamento = 360; // 30 anos em meses
            double taxaJuros = 8.5; // Taxa de juros anual
            double valorParcela = calcularParcela(valorImovel, valorEntrada, prazoFinanciamento, taxaJuros);
            LocalDate dataSimulacao = LocalDate.now();

            String insertSql = "INSERT INTO financiamento_imovel (nome_cliente, valor_imovel, valor_entrada, " +
                    "prazo_financiamento, taxa_juros, valor_parcela, data_simulacao) VALUES (" +
                    "'" + nomeCliente + "', " +
                    valorImovel + ", " +
                    valorEntrada + ", " +
                    prazoFinanciamento + ", " +
                    taxaJuros + ", " +
                    valorParcela + ", " +
                    "'" + dataSimulacao + "')";
            stmt.executeUpdate(insertSql);
            System.out.println("Simulação de financiamento inserida com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ou executar consulta: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC não encontrado: " + e.getMessage());
        } finally {
            // Fechar os recursos
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
        }
    }

    // Método para calcular o valor da parcela
    private static double calcularParcela(double valorImovel, double valorEntrada, int prazo, double taxaJuros) {
        double valorFinanciado = valorImovel - valorEntrada;
        double taxaMensal = (taxaJuros / 100) / 12;
        double parcela = valorFinanciado * (taxaMensal * Math.pow(1 + taxaMensal, prazo)) / (Math.pow(1 + taxaMensal, prazo) - 1);
        return parcela;
    }
}