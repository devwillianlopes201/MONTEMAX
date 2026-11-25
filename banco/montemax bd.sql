CREATE SCHEMA IF NOT EXISTS `montemax` DEFAULT CHARACTER SET utf8 ;
USE `montemax` ;

-- -----------------------------------------------------
-- Table `montemax`.`usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`usuario` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NOT NULL,
  `email` VARCHAR(150) NOT NULL,
  `senha` VARCHAR(255) NOT NULL,
  `foto_perfil` VARCHAR(255) NULL,
  `tipo_usuario` VARCHAR(45) NOT NULL,
  `status_conta` VARCHAR(45) NOT NULL DEFAULT 'ATIVO',
  PRIMARY KEY (`id_usuario`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`profissional`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`profissional` (
  `id_profissional` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `whatsapp` VARCHAR(20) NOT NULL,
  `descricao` TEXT NULL,
  `cidade` VARCHAR(100) NOT NULL,
  `latitude` DECIMAL(10,8) NULL,
  `longitude` DECIMAL(11,8) NULL,
  `raio_atendimento_km` INT NULL,
  `status_cadastro` VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
  `data_cadastro` DATETIME NOT NULL,
  `rg_frente` VARCHAR(500) NOT NULL,
  `rg_verso` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_profissional`),
  UNIQUE INDEX `id_usuario_UNIQUE` (`id_usuario` ASC) ,
  CONSTRAINT `fk_profissional_usuario`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `montemax`.`usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`categoria`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`categoria` (
  `id_categoria` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(50) NOT NULL,
  `descricao` VARCHAR(255) NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE INDEX `nome_UNIQUE` (`nome` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`servico`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`servico` (
  `id_servico` INT NOT NULL AUTO_INCREMENT,
  `nome` VARCHAR(100) NOT NULL,
  `descricao` VARCHAR(255) NULL,
  PRIMARY KEY (`id_servico`),
  UNIQUE INDEX `nome_UNIQUE` (`nome` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`profissional_servico`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`profissional_servico` (
  `id_profissional_servico` INT NOT NULL AUTO_INCREMENT,
  `id_profissional` INT NOT NULL,
  `id_servico` INT NOT NULL,
  PRIMARY KEY (`id_profissional_servico`),
  INDEX `fk_profissional_servico_profissional1_idx` (`id_profissional` ASC) ,
  INDEX `fk_profissional_servico_servico1_idx` (`id_servico` ASC) ,
  UNIQUE INDEX `uq_profissional_servico` (`id_profissional` ASC, `id_servico` ASC) ,
  CONSTRAINT `fk_profissional_servico_profissional1`
    FOREIGN KEY (`id_profissional`)
    REFERENCES `montemax`.`profissional` (`id_profissional`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_profissional_servico_servico1`
    FOREIGN KEY (`id_servico`)
    REFERENCES `montemax`.`servico` (`id_servico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`avaliacao`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`avaliacao` (
  `id_avaliacao` INT NOT NULL AUTO_INCREMENT,
  `nota` TINYINT NOT NULL,
  `data_avaliacao` DATETIME NOT NULL,
  `id_usuario` INT NOT NULL,
  `id_profissional` INT NOT NULL,
  `comentario` TEXT NULL,
  PRIMARY KEY (`id_avaliacao`),
  INDEX `fk_avaliacao_profissional1_idx` (`id_profissional` ASC) ,
  INDEX `fk_avaliacao_usuario1_idx` (`id_usuario` ASC) ,
  CONSTRAINT `fk_avaliacao_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `montemax`.`usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_avaliacao_profissional1`
    FOREIGN KEY (`id_profissional`)
    REFERENCES `montemax`.`profissional` (`id_profissional`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`publicacao`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`publicacao` (
  `id_publicacao` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `titulo` VARCHAR(150) NOT NULL,
  `conteudo` TEXT NOT NULL,
  `data_publicacao` DATETIME NOT NULL,
  PRIMARY KEY (`id_publicacao`),
  INDEX `fk_publicacao_usuario1_idx` (`id_usuario` ASC) ,
  CONSTRAINT `fk_publicacao_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `montemax`.`usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`foto_publicacao`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`foto_publicacao` (
  `id_foto` INT NOT NULL AUTO_INCREMENT,
  `id_publicacao` INT NOT NULL,
  `imagem_url` VARCHAR(255) NOT NULL,
  `descricao` VARCHAR(255) NULL,
  PRIMARY KEY (`id_foto`),
  INDEX `fk_foto_publicacao_publicacao1_idx` (`id_publicacao` ASC) ,
  CONSTRAINT `fk_foto_publicacao_publicacao1`
    FOREIGN KEY (`id_publicacao`)
    REFERENCES `montemax`.`publicacao` (`id_publicacao`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`comentario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`comentario` (
  `id_comentario` INT NOT NULL AUTO_INCREMENT,
  `id_publicacao` INT NOT NULL,
  `id_usuario` INT NOT NULL,
  `conteudo` TEXT NOT NULL,
  `data_comentario` DATETIME NOT NULL,
  PRIMARY KEY (`id_comentario`),
  INDEX `fk_comentario_publicacao1_idx` (`id_publicacao` ASC) ,
  INDEX `fk_comentario_usuario1_idx` (`id_usuario` ASC) ,
  CONSTRAINT `fk_comentario_publicacao1`
    FOREIGN KEY (`id_publicacao`)
    REFERENCES `montemax`.`publicacao` (`id_publicacao`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comentario_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `montemax`.`usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`solicitacao_servico`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`solicitacao_servico` (
  `id_solicitacao_servico` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `id_profissional` INT NOT NULL,
  `id_servico` INT NOT NULL,
  `descricao_solicitacao` VARCHAR(500) NOT NULL,
  `data_solicitacao` DATETIME NOT NULL,
  `status_solicitacao` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`id_solicitacao_servico`),
  INDEX `fk_solicitacao_servico_usuario1_idx` (`id_usuario` ASC) ,
  INDEX `fk_solicitacao_servico_profissional1_idx` (`id_profissional` ASC) ,
  INDEX `fk_solicitacao_servico_servico1_idx` (`id_servico` ASC) ,
  CONSTRAINT `fk_solicitacao_servico_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `montemax`.`usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_solicitacao_servico_profissional1`
    FOREIGN KEY (`id_profissional`)
    REFERENCES `montemax`.`profissional` (`id_profissional`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_solicitacao_servico_servico1`
    FOREIGN KEY (`id_servico`)
    REFERENCES `montemax`.`servico` (`id_servico`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`administrador`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`administrador` (
  `id_administrador` INT NOT NULL AUTO_INCREMENT,
  `id_usuario` INT NOT NULL,
  `nivel_acesso` VARCHAR(30) NOT NULL,
  `data_cadastro` DATETIME NOT NULL,
  PRIMARY KEY (`id_administrador`),
  UNIQUE INDEX `id_usuario_UNIQUE` (`id_usuario` ASC) ,
  CONSTRAINT `fk_administrador_usuario1`
    FOREIGN KEY (`id_usuario`)
    REFERENCES `montemax`.`usuario` (`id_usuario`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `montemax`.`profissional_categoria`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `montemax`.`profissional_categoria` (
  `id_profissional_categoria` INT NOT NULL AUTO_INCREMENT,
  `id_profissional` INT NOT NULL,
  `id_categoria` INT NOT NULL,
  PRIMARY KEY (`id_profissional_categoria`),
  INDEX `fk_profissional_categoria_profissional_idx` (`id_profissional` ASC) ,
  INDEX `fk_profissional_categoria_categoria_idx` (`id_categoria` ASC) ,
  CONSTRAINT `fk_profissional_categoria_profissional`
    FOREIGN KEY (`id_profissional`)
    REFERENCES `montemax`.`profissional` (`id_profissional`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_profissional_categoria_categoria`
    FOREIGN KEY (`id_categoria`)
    REFERENCES `montemax`.`categoria` (`id_categoria`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

