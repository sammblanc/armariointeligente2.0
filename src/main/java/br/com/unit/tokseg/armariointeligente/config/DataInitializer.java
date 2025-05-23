package br.com.unit.tokseg.armariointeligente.config;

import br.com.unit.tokseg.armariointeligente.model.*;
import br.com.unit.tokseg.armariointeligente.repository.*;
import org.slf4j.Logger; // Importação para o Logger
import org.slf4j.LoggerFactory; // Importação para o LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.transaction.annotation.Transactional; // Opcional: para transacionalidade

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    // Logger SLF4J
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CondominioRepository condominioRepository;

    @Autowired
    private ArmarioRepository armarioRepository;

    @Autowired
    private CompartimentoRepository compartimentoRepository;

    @Autowired
    private EntregaRepository entregaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    // @Transactional // Opcional: Descomente se quiser que toda a inicialização seja uma única transação
    public CommandLineRunner initData() {
        return args -> {
            // Adiciona alguns tipos de usuário iniciais
            if (tipoUsuarioRepository.count() == 0) {
                logger.info("Iniciando carregamento de dados iniciais para o sistema SmartLocker...");

                // Criando tipos de usuário
                logger.info("Criando Tipos de Usuário...");
                TipoUsuario admin = new TipoUsuario();
                admin.setNome("Administrador");
                admin.setDescricao("Usuário com acesso total ao sistema");
                admin = tipoUsuarioRepository.save(admin);

                TipoUsuario cliente = new TipoUsuario();
                cliente.setNome("Cliente");
                cliente.setDescricao("Usuário com acesso limitado ao sistema");
                cliente = tipoUsuarioRepository.save(cliente);

                TipoUsuario funcionario = new TipoUsuario();
                funcionario.setNome("Funcionário");
                funcionario.setDescricao("Funcionário da empresa com acesso intermediário");
                funcionario = tipoUsuarioRepository.save(funcionario);

                TipoUsuario entregador = new TipoUsuario();
                entregador.setNome("Entregador");
                entregador.setDescricao("Entregador com acesso administrativo ao sistema");
                entregador = tipoUsuarioRepository.save(entregador);
                logger.info("Tipos de usuário criados com sucesso!");

                // Criando usuários
                // Nota sobre a lógica: Usuários só serão criados aqui se Tipos de Usuário também estiverem vazios.
                // Se desejar que cada bloco seja verificado independentemente, separe os 'if (repository.count() == 0)'.
                if (usuarioRepository.count() == 0) {
                    logger.info("Criando Usuários...");
                    Usuario adminUser = new Usuario();
                    adminUser.setNome("Admin Sistema");
                    adminUser.setEmail("admin@smartlocker.com");
                    adminUser.setSenha(passwordEncoder.encode("admin123"));
                    adminUser.setTelefone("(81) 99999-0000");
                    adminUser.setTipoUsuario(admin);
                    usuarioRepository.save(adminUser);

                    Usuario clienteUser = new Usuario();
                    clienteUser.setNome("João Silva");
                    clienteUser.setEmail("joao.silva@exemplo.com");
                    clienteUser.setSenha(passwordEncoder.encode("senha123"));
                    clienteUser.setTelefone("(81) 98888-1111");
                    clienteUser.setTipoUsuario(cliente);
                    usuarioRepository.save(clienteUser);

                    Usuario entregadorUser = new Usuario();
                    entregadorUser.setNome("Maria Oliveira");
                    entregadorUser.setEmail("maria.oliveira@exemplo.com");
                    entregadorUser.setSenha(passwordEncoder.encode("senha456"));
                    entregadorUser.setTelefone("(81) 97777-2222");
                    entregadorUser.setTipoUsuario(entregador);
                    usuarioRepository.save(entregadorUser);
                    logger.info("Usuários criados com sucesso!");

                    if (condominioRepository.count() == 0) {
                        logger.info("Criando Condomínios...");
                        Condominio condominio1 = new Condominio();
                        condominio1.setNome("Residencial Parque das Flores");
                        condominio1.setEndereco("Av. Principal, 1000");
                        condominio1.setCep("50000-000");
                        condominio1.setCidade("Recife");
                        condominio1.setEstado("PE");
                        condominio1.setTelefone("(81) 3333-4444");
                        condominio1.setEmail("contato@parquedasflores.com");
                        condominio1 = condominioRepository.save(condominio1);

                        Condominio condominio2 = new Condominio();
                        condominio2.setNome("Edifício Solar das Palmeiras");
                        condominio2.setEndereco("Rua das Palmeiras, 500");
                        condominio2.setCep("50010-200");
                        condominio2.setCidade("Recife");
                        condominio2.setEstado("PE");
                        condominio2.setTelefone("(81) 3333-5555");
                        condominio2.setEmail("contato@solardaspalmeiras.com");
                        condominio2 = condominioRepository.save(condominio2);
                        logger.info("Condomínios criados com sucesso!");

                        if (armarioRepository.count() == 0) {
                            logger.info("Criando Armários...");
                            Armario armario1 = new Armario();
                            armario1.setIdentificacao("ARM-001");
                            armario1.setLocalizacao("Hall de entrada");
                            armario1.setDescricao("Armário principal do Condomínio Parque das Flores");
                            armario1.setAtivo(true);
                            armario1.setCondominio(condominio1);
                            armario1 = armarioRepository.save(armario1);

                            Armario armario2 = new Armario();
                            armario2.setIdentificacao("ARM-002");
                            armario2.setLocalizacao("Portaria");
                            armario2.setDescricao("Armário secundário do Condomínio Parque das Flores");
                            armario2.setAtivo(true);
                            armario2.setCondominio(condominio1);
                            armario2 = armarioRepository.save(armario2);

                            Armario armario3 = new Armario();
                            armario3.setIdentificacao("ARM-001"); // Identificação pode se repetir para condomínios diferentes
                            armario3.setLocalizacao("Entrada principal");
                            armario3.setDescricao("Armário principal do Condomínio Solar das Palmeiras");
                            armario3.setAtivo(true);
                            armario3.setCondominio(condominio2);
                            armario3 = armarioRepository.save(armario3);
                            logger.info("Armários criados com sucesso!");

                            if (compartimentoRepository.count() == 0) {
                                logger.info("Criando Compartimentos...");
                                // Compartimentos para o armário 1 (ARM-001 do Parque das Flores)
                                for (int i = 1; i <= 6; i++) { // CORRIGIDO: &lt;= para <=
                                    Compartimento compartimento = new Compartimento();
                                    compartimento.setNumero("A" + i);
                                    // CORRIGIDO: &lt;= para <=
                                    compartimento.setTamanho(i <= 2 ? "P" : (i <= 4 ? "M" : "G"));
                                    compartimento.setOcupado(false);
                                    compartimento.setCodigoAcesso("123456"); // Código de acesso de exemplo
                                    compartimento.setArmario(armario1);
                                    compartimentoRepository.save(compartimento);
                                }

                                // Compartimentos para o armário 2 (ARM-002 do Parque das Flores)
                                for (int i = 1; i <= 4; i++) { // CORRIGIDO: &lt;= para <=
                                    Compartimento compartimento = new Compartimento();
                                    compartimento.setNumero("B" + i);
                                    // CORRIGIDO: &lt;= para <=
                                    compartimento.setTamanho(i <= 2 ? "P" : "M");
                                    compartimento.setOcupado(false);
                                    compartimento.setCodigoAcesso("654321"); // Código de acesso de exemplo
                                    compartimento.setArmario(armario2);
                                    compartimentoRepository.save(compartimento);
                                }

                                // Compartimentos para o armário 3 (ARM-001 do Solar das Palmeiras)
                                for (int i = 1; i <= 8; i++) { // CORRIGIDO: &lt;= para <=
                                    Compartimento compartimento = new Compartimento();
                                    compartimento.setNumero("C" + i);
                                    // CORRIGIDO: &lt;= para <=
                                    compartimento.setTamanho(i <= 3 ? "P" : (i <= 6 ? "M" : "G"));
                                    compartimento.setOcupado(false);
                                    compartimento.setCodigoAcesso("987654"); // Código de acesso de exemplo
                                    compartimento.setArmario(armario3);
                                    compartimentoRepository.save(compartimento);
                                }
                                logger.info("Compartimentos criados com sucesso!");

                                // Criando algumas entregas e reservas de exemplo
                                logger.info("Criando Entregas e Reservas de exemplo...");
                                Compartimento compartimentoA1 = compartimentoRepository.findByNumeroAndArmarioId("A1", armario1.getId()).orElse(null);
                                Compartimento compartimentoB1 = compartimentoRepository.findByNumeroAndArmarioId("B1", armario2.getId()).orElse(null);
                                // Adicionando busca por um compartimento no armario3 para exemplo
                                Compartimento compartimentoC1 = compartimentoRepository.findByNumeroAndArmarioId("C1", armario3.getId()).orElse(null);


                                if (compartimentoA1 != null && clienteUser != null && entregadorUser != null) {
                                    Entrega entrega = new Entrega();
                                    entrega.setCodigoRastreio("BR123456789");
                                    entrega.setDataEntrega(LocalDateTime.now().minusDays(1));
                                    entrega.setStatus(StatusEntrega.ENTREGUE);
                                    entrega.setObservacao("Pacote frágil");
                                    entrega.setCompartimento(compartimentoA1);
                                    entrega.setEntregador(entregadorUser);
                                    entrega.setDestinatario(clienteUser);
                                    entregaRepository.save(entrega);

                                    compartimentoA1.setOcupado(true);
                                    compartimentoRepository.save(compartimentoA1);
                                    logger.info("Entrega de exemplo para {} criada no compartimento {} do armário {}", clienteUser.getNome(), compartimentoA1.getNumero(), armario1.getIdentificacao());
                                } else {
                                    logger.warn("Não foi possível criar entrega de exemplo: compartimentoA1, clienteUser ou entregadorUser não encontrado(s).");
                                }

                                if (compartimentoB1 != null && clienteUser != null) {
                                    Reserva reserva = new Reserva();
                                    reserva.setDataInicio(LocalDateTime.now().plusHours(2)); // Ajustado para evitar conflito de data/hora
                                    reserva.setDataFim(LocalDateTime.now().plusDays(1).plusHours(2));
                                    reserva.setStatus(StatusReserva.CONFIRMADA);
                                    reserva.setObservacao("Reserva para recebimento de encomenda futura");
                                    reserva.setCompartimento(compartimentoB1);
                                    reserva.setUsuario(clienteUser);
                                    reservaRepository.save(reserva);

                                    // Não marcar como ocupado na reserva até a entrega efetiva, ou dependendo da regra de negócio
                                    // compartimentoB1.setOcupado(true);
                                    // compartimentoRepository.save(compartimentoB1);
                                    logger.info("Reserva de exemplo para {} criada no compartimento {} do armário {}", clienteUser.getNome(), compartimentoB1.getNumero(), armario2.getIdentificacao());
                                } else {
                                    logger.warn("Não foi possível criar reserva de exemplo: compartimentoB1 ou clienteUser não encontrado(s).");
                                }
                                logger.info("Entregas e reservas de exemplo processadas.");
                            }
                        }
                    }
                }
                logger.info("Carregamento de dados iniciais concluído com sucesso!");
            } else {
                logger.info("Dados iniciais já existentes. Nenhum dado novo foi carregado.");
            }
        };
    }
}