-- phpMyAdmin SQL Dump
-- version 3.2.3
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generato il: 16 nov, 2009 at 03:52 PM
-- Versione MySQL: 5.1.37
-- Versione PHP: 5.2.10-2ubuntu6.1

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `urpCrmProtDBSvil2`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `cittadino`
--

CREATE TABLE IF NOT EXISTS `cittadino` (
  `nome_cittadino` varchar(45) DEFAULT NULL,
  `cognome_cittadino` varchar(45) DEFAULT NULL,
  `telefono_cittadino` varchar(45) DEFAULT NULL,
  `email_cittadino` varchar(45) DEFAULT NULL,
  `codice_fiscale` varchar(16) NOT NULL,
  `cod_cittadino` varchar(108) NOT NULL DEFAULT '0',
  `id_cittadino` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id_cittadino`),
  UNIQUE KEY `cod_fis_unique` (`codice_fiscale`),
  KEY `new_index` (`cod_cittadino`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dump dei dati per la tabella `cittadino`
--

INSERT INTO `cittadino` (`nome_cittadino`, `cognome_cittadino`, `telefono_cittadino`, `email_cittadino`, `codice_fiscale`, `cod_cittadino`, `id_cittadino`) VALUES
('tyutyututututuy', 'ghjhghjk', '343434', 'mavellino@example.com', '98989898989899', 'tyutyututututuy-ghjhghjk-98989898989899', 1),
('daniele', 'carbone', '47474747474', 'dcarbone@example.com', 'rrrrrrrrrrrr', 'daniele-carbone-rrrrrrrrrrrr', 4);

-- --------------------------------------------------------

--
-- Struttura della tabella `cittadino_pratica`
--

CREATE TABLE IF NOT EXISTS `cittadino_pratica` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cod_cittadino` varchar(108) NOT NULL,
  `id_pratica` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_pratica` (`id_pratica`),
  KEY `cod_cittadino` (`cod_cittadino`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Dump dei dati per la tabella `cittadino_pratica`
--


-- --------------------------------------------------------

--
-- Struttura della tabella `new`
--

CREATE TABLE IF NOT EXISTS `new` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `new_check` varchar(2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `new_check` (`new_check`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dump dei dati per la tabella `new`
--

INSERT INTO `new` (`id`, `new_check`) VALUES
(1, 'NO'),
(2, 'SI');

-- --------------------------------------------------------

--
-- Struttura della tabella `pratica`
--

CREATE TABLE IF NOT EXISTS `pratica` (
  `id_pratica` int(11) NOT NULL AUTO_INCREMENT,
  `data_creazione_pratica` datetime NOT NULL,
  `descrizione` varchar(50) NOT NULL,
  `descrizione_accurata` text,
  `soluzione` text,
  `priorita` varchar(255) DEFAULT NULL,
  `stato` varchar(255) DEFAULT 'APERTA',
  `assegnataA` varchar(50) DEFAULT NULL,
  `tipologia_pratica` varchar(100) DEFAULT NULL,
  `ci_sono_novita` varchar(2) DEFAULT 'SI',
  `nome_cittadino` varchar(45) DEFAULT NULL,
  `cognome_cittadino` varchar(45) DEFAULT NULL,
  `telefono_cittadino` varchar(100) DEFAULT NULL,
  `email_cittadino` varchar(100) DEFAULT NULL,
  `c_fiscale_cittadino` varchar(16) DEFAULT NULL,
  `note_cittadino` text,
  `operatore` varchar(100) DEFAULT NULL,
  `tags` text,
  PRIMARY KEY (`id_pratica`),
  KEY `new_index` (`stato`) USING BTREE,
  KEY `tipologia_pratica` (`tipologia_pratica`),
  KEY `priorita` (`priorita`),
  KEY `assegnataA` (`assegnataA`),
  KEY `new` (`ci_sono_novita`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=9552059 ;

--
-- Dump dei dati per la tabella `pratica`
--

INSERT INTO `pratica` (`id_pratica`, `data_creazione_pratica`, `descrizione`, `descrizione_accurata`, `soluzione`, `priorita`, `stato`, `assegnataA`, `tipologia_pratica`, `ci_sono_novita`, `nome_cittadino`, `cognome_cittadino`, `telefono_cittadino`, `email_cittadino`, `c_fiscale_cittadino`, `note_cittadino`, `operatore`, `tags`) VALUES
(17, '1957-12-13 00:00:00', 'asd', NULL, NULL, '0_DA_CLASSIFICARE', 'CHIUSA', 'ufficio_legislativo', '1. benefici - autonoma sistemazione', 'SI', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL),
(23, '2003-12-07 00:00:00', 'sssss', '4343', 'soluzione richiesta', '0_DA_CLASSIFICARE', 'ASSEGNATA-A-FONTI', 'backoffice', NULL, 'SI', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL),
(28, '2004-04-03 00:00:00', 'asd', 'w', NULL, '1_MIGLIORIA', 'ASSEGNATA-A-FONTI', 'ufficio_legislativo', NULL, 'SI', NULL, NULL, NULL, NULL, NULL, NULL, '123456', NULL),
(9552045, '2009-10-05 00:00:00', 'prova', 'prova', 'provaTEST', '2_IMPORTANTE', 'DA-CONTATTARE-PER-APPROFONDIMENTI', NULL, '3. alloggi - alloggi universitari ', 'NO', NULL, NULL, NULL, NULL, NULL, NULL, 'prova', 'case gas'),
(9552046, '2009-10-06 00:00:00', 'richiesta casa', 'la signora vuole sapere cosa deve fare per ottenere una casa.', 'basta fare richiesta all''urp regionale bla bla', '2_IMPORTANTE', 'DA-CONTATTARE-PER-APPROFONDIMENTI', 'ufficio_logistico', '3. alloggi - C.A.S.E ', 'SI', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'richiesta casqa'),
(9552047, '2009-10-06 00:00:00', 'richiesta sanitaria', 'Il signor izio ha bisogno di una sedia a rotelle', 'effettueremo domani mattina la consegna', '3_URGENTE', 'DA-CONTATTARE-PER-APPROFONDIMENTI', 'ufficio_logistico', '4. servizi - salute', 'SI', NULL, NULL, NULL, NULL, NULL, NULL, 'sempronio', 'sanità sedia rotelle'),
(9552048, '2009-10-06 00:00:00', 'allaccio telefono', NULL, NULL, '0_DA_CLASSIFICARE', 'CHIUSA', NULL, NULL, 'SI', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9552049, '2009-10-07 00:00:00', 'apertura negozio', NULL, 'paga le tasse di apertura23', '0_DA_CLASSIFICARE', 'RISOLTA-DA-FONTE', 'backoffice', '3. alloggi - affitti', 'SI', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9552050, '2009-10-14 00:00:00', 'richiesta patente', NULL, NULL, '0_DA_CLASSIFICARE', 'CHIUSA', NULL, NULL, 'SI', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9552051, '2009-10-15 00:00:00', 'cancellazione ipoteca', NULL, NULL, '0_DA_CLASSIFICARE', 'APERTA', NULL, '4. servizi - salute', 'SI', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9552052, '2009-10-07 00:00:00', 'test minimale', NULL, NULL, '0_DA_CLASSIFICARE', 'CHIUSA', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9552053, '2009-10-07 00:00:00', 'testmin', NULL, NULL, '0_DA_CLASSIFICARE', 'CHIUSA', NULL, NULL, 'SI', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9552054, '2009-10-07 00:00:00', 'test456', NULL, NULL, '0_DA_CLASSIFICARE', 'CHIUSA', NULL, NULL, 'SI', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9552055, '2009-10-07 00:00:00', 'problema logistico', NULL, 'ok basta chiamare il numero 123123123', '0_DA_CLASSIFICARE', 'RISOLTA-DA-FONTE', 'backoffice', '4. servizi - servizi essenziali ', 'SI', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9552056, '2009-10-07 00:00:00', 'richiesta dentiera', NULL, 'cukident', '3_URGENTE', 'CHIUSA', 'backoffice', '2. ricostruzione - case A ', 'SI', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(9552057, '2009-10-08 00:00:00', 'richiesta di ospitalità', NULL, 'richiesta accettata', NULL, 'RISOLTA-DA-FONTE', 'backoffice', '3. alloggi - alloggi universitari ', 'SI', 'tizio', 'caio', NULL, NULL, NULL, NULL, NULL, NULL),
(9552058, '2009-10-08 00:00:00', 'another tesd', NULL, NULL, NULL, 'RISOLTA-DA-FONTE', 'backoffice', '1. benefici - altro', 'SI', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Struttura della tabella `pratica_settore`
--

CREATE TABLE IF NOT EXISTS `pratica_settore` (
  `id_pratica_settore` int(11) NOT NULL AUTO_INCREMENT,
  `id_pratica` int(11) NOT NULL,
  `nome_settore` varchar(45) NOT NULL,
  PRIMARY KEY (`id_pratica_settore`),
  KEY `new_index` (`id_pratica`),
  KEY `nome_settore` (`nome_settore`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dump dei dati per la tabella `pratica_settore`
--

INSERT INTO `pratica_settore` (`id_pratica_settore`, `id_pratica`, `nome_settore`) VALUES
(2, 17, 'ufficio_logistico');

-- --------------------------------------------------------

--
-- Struttura della tabella `priorita`
--

CREATE TABLE IF NOT EXISTS `priorita` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `livello` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `livello` (`livello`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=6 ;

--
-- Dump dei dati per la tabella `priorita`
--

INSERT INTO `priorita` (`id`, `livello`) VALUES
(1, '0_DA_CLASSIFICARE'),
(2, '1_MIGLIORIA'),
(3, '2_IMPORTANTE'),
(4, '3_URGENTE'),
(5, '4_BLOCCANTE');

-- --------------------------------------------------------

--
-- Struttura della tabella `settore`
--

CREATE TABLE IF NOT EXISTS `settore` (
  `id_settore` int(11) NOT NULL AUTO_INCREMENT,
  `nome_settore` varchar(55) DEFAULT NULL,
  `mail_settore` varchar(255) NOT NULL,
  PRIMARY KEY (`id_settore`),
  UNIQUE KEY `unique_nome_settore` (`nome_settore`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

--
-- Dump dei dati per la tabella `settore`
--

INSERT INTO `settore` (`id_settore`, `nome_settore`, `mail_settore`) VALUES
(1, '', ''),
(2, 'ufficio_legislativo', 'ufficiolegislativodpc@example.com'),
(3, 'ufficio_logistico', 'ufficiologisticadpc@example.com'),
(4, 'backoffice', 'backoffice1@example.com');

-- --------------------------------------------------------

--
-- Struttura della tabella `sotto_pratica`
--

CREATE TABLE IF NOT EXISTS `sotto_pratica` (
  `id_sotto_pratica` int(11) NOT NULL AUTO_INCREMENT,
  `descrizione_sotto_pratica` text NOT NULL,
  `pratica_id_pratica` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_sotto_pratica`),
  KEY `pratica_id_pratica` (`pratica_id_pratica`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

--
-- Dump dei dati per la tabella `sotto_pratica`
--

INSERT INTO `sotto_pratica` (`id_sotto_pratica`, `descrizione_sotto_pratica`, `pratica_id_pratica`) VALUES
(1, 'testo', 23),
(2, 'test2', 28),
(3, 'test3', 28);

-- --------------------------------------------------------

--
-- Struttura della tabella `statoPratica`
--

CREATE TABLE IF NOT EXISTS `statoPratica` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `stato` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `stato` (`stato`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

--
-- Dump dei dati per la tabella `statoPratica`
--

INSERT INTO `statoPratica` (`id`, `stato`) VALUES
(1, 'APERTA'),
(2, 'ASSEGNATA-A-FONTI'),
(3, 'CHIUSA'),
(4, 'DA-CONTATTARE-PER-APPROFONDIMENTI'),
(5, 'DA-CONTATTARE-PER-CHIUSURA'),
(6, 'IN-LAVORAZIONE'),
(7, 'RISOLTA-DA-FONTE');

-- --------------------------------------------------------

--
-- Struttura della tabella `tag`
--

CREATE TABLE IF NOT EXISTS `tag` (
  `etichetta` varchar(100) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  KEY `new_index` (`etichetta`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=8 ;

--
-- Dump dei dati per la tabella `tag`
--

INSERT INTO `tag` (`etichetta`, `id`) VALUES
('cittadinanza', 1),
('immobili', 2),
('prestiti', 3),
('sanita', 4),
('scuola', 5),
('tasse', 6),
('veicoli', 7);

-- --------------------------------------------------------

--
-- Struttura della tabella `tag_pratica`
--

CREATE TABLE IF NOT EXISTS `tag_pratica` (
  `id_tag_pratica` int(11) NOT NULL AUTO_INCREMENT,
  `id_pratica` int(11) NOT NULL,
  `etichetta_tag` varchar(100) NOT NULL,
  PRIMARY KEY (`id_tag_pratica`),
  KEY `id_pratica` (`id_pratica`),
  KEY `etichetta_tag` (`etichetta_tag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Dump dei dati per la tabella `tag_pratica`
--


-- --------------------------------------------------------

--
-- Struttura della tabella `testNull`
--

CREATE TABLE IF NOT EXISTS `testNull` (
  `id` int(11) NOT NULL,
  `campo` blob,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `testNull`
--

INSERT INTO `testNull` (`id`, `campo`) VALUES
(1, 0x7465737431),
(2, 0x7465737432);

-- --------------------------------------------------------

--
-- Struttura della tabella `tipologia_pratica`
--

CREATE TABLE IF NOT EXISTS `tipologia_pratica` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tipologia` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tipologia` (`tipologia`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=27 ;

--
-- Dump dei dati per la tabella `tipologia_pratica`
--

INSERT INTO `tipologia_pratica` (`id`, `tipologia`) VALUES
(1, ''),
(22, '1. benefici - altro'),
(2, '1. benefici - autonoma sistemazione'),
(3, '1. benefici - carta acquisti '),
(4, '1. benefici - indennità e benefici vari'),
(23, '2. ricostruzione - altro'),
(5, '2. ricostruzione - case A '),
(6, '2. ricostruzione - case B C '),
(7, '2. ricostruzione - Case E'),
(8, '3. alloggi - affitti'),
(9, '3. alloggi - alberghi '),
(10, '3. alloggi - alloggi universitari '),
(24, '3. alloggi - altro'),
(11, '3. alloggi - C.A.S.E '),
(12, '3. alloggi - MAP '),
(25, '4. servizi - altro'),
(13, '4. servizi - salute'),
(14, '4. servizi - scuola '),
(15, '4. servizi - servizi essenziali '),
(16, '4. servizi - trasporti '),
(17, '4. servizi - università '),
(18, '5. attività produttive'),
(26, '6. generiche - altro'),
(19, '6. generiche - consegna documenti'),
(20, '6. generiche - richiesta informazioni generali'),
(21, '6. generiche - richiesta informazioni specifiche');

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_field`
--

CREATE TABLE IF NOT EXISTS `__system_field` (
  `id` int(11) NOT NULL,
  `default_header` tinyint(1) NOT NULL,
  `search_grouping` tinyint(1) NOT NULL DEFAULT '1',
  `id_resultset` int(11) NOT NULL,
  `id_grouping` int(11) NOT NULL DEFAULT '1',
  `type` varchar(50) NOT NULL DEFAULT 'VARCHAR',
  `defaultvalue` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`,`id_resultset`),
  KEY `id_raggruppamento` (`id_grouping`),
  KEY `id_resultset` (`id_resultset`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='search_grouping=0 ==> ricerca base';

--
-- Dump dei dati per la tabella `__system_field`
--

INSERT INTO `__system_field` (`id`, `default_header`, `search_grouping`, `id_resultset`, `id_grouping`, `type`, `defaultvalue`) VALUES
(9629337, 1, 1, 9629336, 1, 'int', ''),
(9629338, 1, 1, 9629336, 1, 'datetime', ''),
(9629339, 1, 1, 9629336, 1, 'string', ''),
(9629340, 1, 1, 9629336, 1, 'blob', ''),
(9629341, 1, 1, 9629336, 1, 'blob', ''),
(9629342, 1, 1, 9629336, 1, 'string', ''),
(9629343, 1, 1, 9629336, 1, 'string', ''),
(9629344, 1, 1, 9629336, 1, 'string', ''),
(9629345, 1, 1, 9629336, 1, 'string', ''),
(9629346, 1, 1, 9629336, 1, 'string', ''),
(9629347, 1, 1, 9629336, 1, 'string', ''),
(9629348, 1, 1, 9629336, 1, 'string', ''),
(9629349, 1, 1, 9629336, 1, 'string', ''),
(9629350, 1, 1, 9629336, 1, 'string', ''),
(9629351, 1, 1, 9629336, 1, 'string', ''),
(9629352, 1, 1, 9629336, 1, 'blob', ''),
(9629353, 1, 1, 9629336, 1, 'string', ''),
(9629354, 1, 1, 9629336, 1, 'blob', ''),
(9629356, 1, 1, 9629355, 1, 'int', ''),
(9629357, 1, 1, 9629355, 1, 'datetime', ''),
(9629358, 1, 1, 9629355, 1, 'string', ''),
(9629359, 1, 1, 9629355, 1, 'blob', ''),
(9629360, 1, 1, 9629355, 1, 'blob', ''),
(9629361, 1, 1, 9629355, 1, 'string', ''),
(9629362, 1, 1, 9629355, 1, 'string', ''),
(9629363, 1, 1, 9629355, 1, 'string', ''),
(9629364, 1, 1, 9629355, 1, 'string', ''),
(9629365, 1, 1, 9629355, 1, 'string', ''),
(9629366, 1, 1, 9629355, 1, 'string', ''),
(9629367, 1, 1, 9629355, 1, 'string', ''),
(9629368, 1, 1, 9629355, 1, 'string', ''),
(9629369, 1, 1, 9629355, 1, 'string', ''),
(9629370, 1, 1, 9629355, 1, 'string', ''),
(9629371, 1, 1, 9629355, 1, 'blob', ''),
(9629372, 1, 1, 9629355, 1, 'string', ''),
(9629373, 1, 1, 9629355, 1, 'blob', ''),
(9629375, 1, 1, 9629374, 1, 'int', ''),
(9629376, 1, 1, 9629374, 1, 'blob', ''),
(9629377, 1, 1, 9629374, 1, 'int', ''),
(9629379, 1, 1, 9629378, 1, 'int', ''),
(9629380, 1, 1, 9629378, 1, 'blob', ''),
(9629381, 1, 1, 9629378, 1, 'int', '');

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_fieldinpreference`
--

CREATE TABLE IF NOT EXISTS `__system_fieldinpreference` (
  `id_headerpreference` int(11) NOT NULL,
  `id_field` int(11) NOT NULL,
  PRIMARY KEY (`id_headerpreference`,`id_field`),
  KEY `id_field` (`id_field`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='accoppia una preferenza con i campi preferiti';

--
-- Dump dei dati per la tabella `__system_fieldinpreference`
--


-- --------------------------------------------------------

--
-- Struttura della tabella `__system_group`
--

CREATE TABLE IF NOT EXISTS `__system_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9 ;

--
-- Dump dei dati per la tabella `__system_group`
--

INSERT INTO `__system_group` (`id`, `name`, `status`) VALUES
(1, 'SystemAdminChief', 1),
(2, 'systemAdmin', 1),
(3, 'siteAdmin', 1),
(4, 'readOnlyGroup', 1),
(5, 'legislativo', 1),
(6, 'operators', 1),
(7, 'backOffice', 1),
(8, 'logistico', 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_grouping`
--

CREATE TABLE IF NOT EXISTS `__system_grouping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `alias` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dump dei dati per la tabella `__system_grouping`
--

INSERT INTO `__system_grouping` (`id`, `name`, `alias`) VALUES
(1, 'basegrouping', 'raggruppamento base');

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_groupwarnings`
--

CREATE TABLE IF NOT EXISTS `__system_groupwarnings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(256) NOT NULL,
  `body` text,
  `date` datetime NOT NULL,
  `type` varchar(5) NOT NULL,
  `id_group` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_group` (`id_group`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=5512519 ;

--
-- Dump dei dati per la tabella `__system_groupwarnings`
--

INSERT INTO `__system_groupwarnings` (`id`, `title`, `body`, `date`, `type`, `id_group`) VALUES
(5512518, 'MSJULAGWQJTCKXUOEUDZAWONRCZRQNBBQEPNSEBKPVKZNXOLBJMJRPPOWVIPZXAXCJBVMDITVIMGSVZCFRGCHCIOGOIMRAUZXFXQTLVNUXJTZTHYVNEOYWCSDHGGQBTKUFKKSAMFGIMSFAIYGQWNGHNEMLYSGUXCXNPFDMWSMZQPSQEBJOTZKUOXGZSIHVEEYTNLMJLOXPINURZGMIXZFFIAJQJGDHKHAZVNECYSCZBSNINTBJVNRQLAJHPYYGZO', NULL, '1992-05-01 09:49:04', 'ZSJCS', 7213306);

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_headerpreference`
--

CREATE TABLE IF NOT EXISTS `__system_headerpreference` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `id_user` int(11) NOT NULL,
  `rescuedate` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_user` (`id_user`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 COMMENT='accoppia un utente con le sue preferenze di header' AUTO_INCREMENT=2412952 ;

--
-- Dump dei dati per la tabella `__system_headerpreference`
--

INSERT INTO `__system_headerpreference` (`id`, `name`, `id_user`, `rescuedate`) VALUES
(2412946, 'due campi-1077279566', 2, '2009-10-01 12:06:40'),
(2412947, 'test-140254518', 9, '2009-11-05 17:02:53'),
(2412948, 'test-144901851', 9, '2009-11-05 17:53:32'),
(2412949, 'testant--21351038', 9, '2009-11-06 09:30:36'),
(2412950, 'null--1525712092', 9, '2009-11-10 08:43:56'),
(2412951, 'tesrt--640859841', 9, '2009-11-10 09:17:46');

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_management`
--

CREATE TABLE IF NOT EXISTS `__system_management` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_group` int(11) NOT NULL,
  `id_resource` int(11) NOT NULL,
  `readperm` tinyint(1) NOT NULL,
  `deleteperm` tinyint(1) NOT NULL,
  `modifyperm` tinyint(1) NOT NULL,
  `insertperm` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_gruppo` (`id_group`),
  KEY `id_risorsa` (`id_resource`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1975 ;

--
-- Dump dei dati per la tabella `__system_management`
--

INSERT INTO `__system_management` (`id`, `id_group`, `id_resource`, `readperm`, `deleteperm`, `modifyperm`, `insertperm`) VALUES
(1891, 1, 9629336, 1, 1, 1, 1),
(1892, 1, 9629337, 1, 1, 1, 1),
(1893, 1, 9629338, 1, 1, 1, 1),
(1894, 1, 9629339, 1, 1, 1, 1),
(1895, 1, 9629340, 1, 1, 1, 1),
(1896, 1, 9629341, 1, 1, 1, 1),
(1897, 1, 9629342, 1, 1, 1, 1),
(1898, 1, 9629343, 1, 1, 1, 1),
(1899, 1, 9629344, 1, 1, 1, 1),
(1900, 1, 9629345, 1, 1, 1, 1),
(1901, 1, 9629346, 1, 1, 1, 1),
(1902, 1, 9629347, 1, 1, 1, 1),
(1903, 1, 9629348, 1, 1, 1, 1),
(1904, 1, 9629349, 1, 1, 1, 1),
(1905, 1, 9629350, 1, 1, 1, 1),
(1906, 1, 9629351, 1, 1, 1, 1),
(1907, 1, 9629352, 1, 1, 1, 1),
(1908, 1, 9629353, 1, 1, 1, 1),
(1909, 1, 9629354, 1, 1, 1, 1),
(1910, 6, 9629337, 1, 1, 1, 1),
(1911, 6, 9629338, 1, 1, 0, 1),
(1912, 6, 9629339, 1, 1, 1, 1),
(1913, 6, 9629340, 1, 1, 1, 1),
(1914, 6, 9629341, 1, 1, 1, 1),
(1915, 6, 9629342, 1, 1, 1, 1),
(1916, 6, 9629343, 1, 1, 1, 1),
(1917, 6, 9629344, 1, 1, 1, 1),
(1918, 6, 9629345, 1, 1, 1, 1),
(1919, 6, 9629346, 1, 1, 1, 1),
(1920, 6, 9629347, 1, 1, 1, 1),
(1921, 6, 9629348, 1, 1, 1, 1),
(1922, 6, 9629349, 1, 1, 1, 1),
(1923, 6, 9629350, 1, 1, 1, 1),
(1924, 6, 9629351, 1, 1, 1, 1),
(1925, 6, 9629352, 1, 1, 1, 1),
(1926, 6, 9629353, 1, 1, 1, 1),
(1927, 6, 9629354, 1, 1, 1, 1),
(1928, 6, 9629336, 1, 1, 1, 1),
(1929, 1, 9629355, 1, 1, 1, 1),
(1930, 1, 9629356, 1, 1, 1, 1),
(1931, 1, 9629357, 1, 1, 0, 1),
(1932, 1, 9629358, 1, 1, 1, 1),
(1933, 1, 9629359, 1, 1, 1, 1),
(1934, 1, 9629360, 1, 1, 1, 1),
(1935, 1, 9629361, 1, 1, 1, 1),
(1936, 1, 9629362, 1, 1, 1, 1),
(1937, 1, 9629363, 1, 1, 1, 1),
(1938, 1, 9629364, 1, 1, 1, 1),
(1939, 1, 9629365, 1, 1, 1, 1),
(1940, 1, 9629366, 1, 1, 1, 1),
(1941, 1, 9629367, 1, 1, 1, 1),
(1942, 1, 9629368, 1, 1, 1, 1),
(1943, 1, 9629369, 1, 1, 1, 1),
(1944, 1, 9629370, 1, 1, 1, 1),
(1945, 1, 9629371, 1, 1, 1, 1),
(1946, 1, 9629372, 1, 1, 1, 1),
(1947, 1, 9629373, 1, 1, 1, 1),
(1948, 6, 9629356, 1, 1, 1, 1),
(1949, 6, 9629357, 1, 1, 0, 1),
(1950, 6, 9629358, 1, 1, 1, 1),
(1951, 6, 9629359, 1, 1, 1, 1),
(1952, 6, 9629360, 1, 1, 1, 1),
(1953, 6, 9629361, 1, 1, 1, 1),
(1954, 6, 9629362, 1, 1, 1, 1),
(1955, 6, 9629363, 1, 1, 1, 1),
(1956, 6, 9629364, 1, 1, 1, 1),
(1957, 6, 9629365, 1, 1, 1, 1),
(1958, 6, 9629366, 1, 1, 1, 1),
(1959, 6, 9629367, 1, 1, 1, 1),
(1960, 6, 9629368, 1, 1, 1, 1),
(1961, 6, 9629369, 1, 1, 1, 1),
(1962, 6, 9629370, 1, 1, 1, 1),
(1963, 6, 9629371, 1, 1, 1, 1),
(1964, 6, 9629372, 1, 1, 1, 1),
(1965, 6, 9629373, 1, 1, 1, 1),
(1966, 6, 9629355, 1, 1, 1, 1),
(1967, 6, 9629375, 1, 1, 1, 1),
(1968, 6, 9629376, 1, 1, 1, 1),
(1969, 6, 9629377, 1, 1, 1, 1),
(1970, 6, 9629374, 1, 1, 1, 1),
(1971, 6, 9629379, 1, 1, 1, 1),
(1972, 6, 9629380, 1, 1, 1, 1),
(1973, 6, 9629381, 1, 1, 1, 1),
(1974, 6, 9629378, 1, 1, 1, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_notify`
--

CREATE TABLE IF NOT EXISTS `__system_notify` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address_statement` text NOT NULL,
  `name` varchar(50) NOT NULL,
  `resultset_id` int(11) NOT NULL,
  `data_statement` text NOT NULL,
  `xslt` text NOT NULL,
  `link_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `rs_id` (`resultset_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=24 ;

--
-- Dump dei dati per la tabella `__system_notify`
--


-- --------------------------------------------------------

--
-- Struttura della tabella `__system_plugin`
--

CREATE TABLE IF NOT EXISTS `__system_plugin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `configurationfile` varchar(256) NOT NULL,
  `type` varchar(9) NOT NULL,
  `columns` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'vale 1 per tutte le colonne, vale 0 solo per le visibili; insignificante in caso di plugin "single"',
  `note` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Dump dei dati per la tabella `__system_plugin`
--


-- --------------------------------------------------------

--
-- Struttura della tabella `__system_pluginassociation`
--

CREATE TABLE IF NOT EXISTS `__system_pluginassociation` (
  `id_plugin` int(11) NOT NULL,
  `id_resultset` int(11) NOT NULL,
  PRIMARY KEY (`id_plugin`,`id_resultset`),
  KEY `id_resultset` (`id_resultset`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `__system_pluginassociation`
--


-- --------------------------------------------------------

--
-- Struttura della tabella `__system_resource`
--

CREATE TABLE IF NOT EXISTS `__system_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  `gestible` tinyint(1) NOT NULL DEFAULT '1',
  `alias` varchar(40) DEFAULT NULL,
  `note` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=9629382 ;

--
-- Dump dei dati per la tabella `__system_resource`
--

INSERT INTO `__system_resource` (`id`, `name`, `gestible`, `alias`, `note`) VALUES
(9629312, 'sotto_pratica', 1, 'pratiche amministrative collegate', NULL),
(9629336, 'pratica', 1, 'Pratiche', NULL),
(9629337, 'id_pratica', 1, 'id_pratica', NULL),
(9629338, 'data_creazione_pratica', 1, 'data_creazione_pratica', NULL),
(9629339, 'descrizione', 1, 'descrizione', NULL),
(9629340, 'descrizione_accurata', 1, 'descrizione_accurata', NULL),
(9629341, 'soluzione', 1, 'soluzione', NULL),
(9629342, 'priorita', 1, 'priorita', NULL),
(9629343, 'stato', 1, 'stato', NULL),
(9629344, 'assegnataA', 1, 'assegnataA', NULL),
(9629345, 'tipologia_pratica', 1, 'tipologia_pratica', NULL),
(9629346, 'ci_sono_novita', 1, 'ci_sono_novita', NULL),
(9629347, 'nome_cittadino', 1, 'nome_cittadino', NULL),
(9629348, 'cognome_cittadino', 1, 'cognome_cittadino', NULL),
(9629349, 'telefono_cittadino', 1, 'telefono_cittadino', NULL),
(9629350, 'email_cittadino', 1, 'email_cittadino', NULL),
(9629351, 'c_fiscale_cittadino', 1, 'c_fiscale_cittadino', NULL),
(9629352, 'note_cittadino', 1, 'note_cittadino', NULL),
(9629353, 'operatore', 1, 'operatore', NULL),
(9629354, 'tags', 1, 'tags', NULL),
(9629355, 'pratica', 1, 'Pratiche da ricontattare', NULL),
(9629356, 'id_pratica', 1, 'id_pratica', NULL),
(9629357, 'data_creazione_pratica', 1, 'data_creazione_pratica', NULL),
(9629358, 'descrizione', 1, 'descrizione', NULL),
(9629359, 'descrizione_accurata', 1, 'descrizione_accurata', NULL),
(9629360, 'soluzione', 1, 'soluzione', NULL),
(9629361, 'priorita', 1, 'priorita', NULL),
(9629362, 'stato', 1, 'stato', NULL),
(9629363, 'assegnataA', 1, 'assegnataA', NULL),
(9629364, 'tipologia_pratica', 1, 'tipologia_pratica', NULL),
(9629365, 'ci_sono_novita', 1, 'ci_sono_novita', NULL),
(9629366, 'nome_cittadino', 1, 'nome_cittadino', NULL),
(9629367, 'cognome_cittadino', 1, 'cognome_cittadino', NULL),
(9629368, 'telefono_cittadino', 1, 'telefono_cittadino', NULL),
(9629369, 'email_cittadino', 1, 'email_cittadino', NULL),
(9629370, 'c_fiscale_cittadino', 1, 'c_fiscale_cittadino', NULL),
(9629371, 'note_cittadino', 1, 'note_cittadino', NULL),
(9629372, 'operatore', 1, 'operatore', NULL),
(9629373, 'tags', 1, 'tags', NULL),
(9629374, 'sotto_pratica', 1, 'Sottopratiche', NULL),
(9629375, 'id_sotto_pratica', 1, 'id_sotto_pratica', NULL),
(9629376, 'descrizione_sotto_pratica', 1, 'descrizione_sotto_pratica', NULL),
(9629377, 'pratica_id_pratica', 1, 'pratica_id_pratica', NULL),
(9629378, 'sotto_pratica', 1, 'Sottopratiche Amministrative', NULL),
(9629379, 'id_sotto_pratica', 1, 'id_sotto_pratica', NULL),
(9629380, 'descrizione_sotto_pratica', 1, 'descrizione_sotto_pratica', NULL),
(9629381, 'pratica_id_pratica', 1, 'pratica_id_pratica', NULL);

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_resultset`
--

CREATE TABLE IF NOT EXISTS `__system_resultset` (
  `id` int(11) NOT NULL,
  `statement` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dump dei dati per la tabella `__system_resultset`
--

INSERT INTO `__system_resultset` (`id`, `statement`) VALUES
(9629336, 'select * from pratica'),
(9629355, 'SELECT * FROM `pratica` WHERE stato = "DA-CONTATTARE-PER-APPROFONDIMENTI" OR stato = "DA-CONTATTARE-PER-CHIUSURA" ORDER BY `priorita` DESC, `ci_sono_novita` DESC, `data_creazione_pratica`'),
(9629374, 'SELECT * FROM sotto_pratica'),
(9629378, 'SELECT * FROM sotto_pratica WHERE pratica_id_pratica > 1000');

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_toolbar`
--

CREATE TABLE IF NOT EXISTS `__system_toolbar` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_resultset` int(11) NOT NULL,
  `id_group` int(11) NOT NULL,
  `tools` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_resultset` (`id_resultset`,`id_group`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=65 ;

--
-- Dump dei dati per la tabella `__system_toolbar`
--

INSERT INTO `__system_toolbar` (`id`, `id_resultset`, `id_group`, `tools`) VALUES
(29, 9628931, 7, 'ALL'),
(30, 9628933, 7, 'ALL'),
(31, 9628937, 1, 'ALL'),
(32, 9628937, 7, 'ALL'),
(34, 9628985, 7, 'ALL'),
(36, 9629000, 7, 'ALL'),
(37, 9629075, 6, 'MODIFY EXPORT'),
(38, 9629090, 6, 'MODIFY EXPORT'),
(39, 9629165, 7, 'ALL'),
(40, 9629168, 1, 'ALL'),
(41, 9629172, 7, 'ALL'),
(42, 9629168, 7, 'ALL'),
(43, 9629175, 7, 'ALL'),
(44, 9629195, 7, 'ALL'),
(45, 9629215, 6, 'MODIFY EXPORT'),
(46, 9629235, 6, 'MODIFY EXPORT'),
(47, 9629295, 7, 'ALL'),
(48, 9629299, 1, 'ALL'),
(49, 9629299, 6, 'ALL'),
(50, 9629302, 1, 'ALL'),
(51, 9629302, 6, 'ALL'),
(52, 9629305, 1, 'ALL'),
(53, 9629305, 6, 'ALL'),
(54, 9629308, 1, 'ALL'),
(55, 9629308, 6, 'ALL'),
(57, 9629313, 1, 'ALL'),
(58, 9629313, 6, 'ALL'),
(59, 9629336, 1, 'ALL'),
(60, 9629336, 6, 'ALL'),
(61, 9629355, 1, 'ALL'),
(62, 9629355, 6, 'ALL'),
(63, 9629374, 6, 'ALL'),
(64, 9629378, 6, 'ALL');

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_user`
--

CREATE TABLE IF NOT EXISTS `__system_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL,
  `name` varchar(256) DEFAULT '',
  `surname` varchar(256) DEFAULT '',
  `email` varchar(256) DEFAULT '',
  `office` varchar(256) DEFAULT '',
  `telephone` varchar(20) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `lastlogintime` datetime DEFAULT NULL,
  `logincount` smallint(6) NOT NULL DEFAULT '0',
  `id_group` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `id_gruppo` (`id_group`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=12 ;

--
-- Dump dei dati per la tabella `__system_user`
--

INSERT INTO `__system_user` (`id`, `username`, `password`, `name`, `surname`, `email`, `office`, `telephone`, `status`, `lastlogintime`, `logincount`, `id_group`) VALUES
(1, 'sysadmin', '', '', '', '', '', '', 1, '0000-00-00 00:00:00', 0, 1),
(2, 'demo', '*C142FB215B6E05B7C134B1A653AD4B455157FD79', 'demo', 'demo', 'sysadmin@example.com', 'null', '45764576457', 1, '2009-10-01 18:02:39', 550, 3),
(3, 'readonly', '*922A4B420903CAD4E7FC56A23122AB927E051FE3', 'read only user', '', '', '', '', 1, NULL, 0, 4),
(4, 'sportello1', '*34FE89252BE32A81639E0A2953BBB20AC9456EF1', 'sportello', 'uno', 'test@test.it', 'null', 'null', 1, '2009-11-16 15:49:53', 373, 6),
(5, 'sportello2', '*34FE89252BE32A81639E0A2953BBB20AC9456EF1', 'sportello', '', '', '', '', 1, NULL, 0, 6),
(6, 'ufficiolegislativodpc', '*34FE89252BE32A81639E0A2953BBB20AC9456EF1', 'ufficio', 'legislativo', 'ufficiolegislativodpc@example.com', '', '', 1, '2009-10-07 15:55:45', 7, 5),
(7, 'ufficiologisticadpc', '*34FE89252BE32A81639E0A2953BBB20AC9456EF1', 'ufficio', 'logstico', 'ufficiologisticadpc@example.com', '', '', 1, '2009-10-20 11:12:00', 19, 8),
(8, 'backOffice2', '*34FE89252BE32A81639E0A2953BBB20AC9456EF1', 'backOffice', '2', '', '', '', 1, NULL, 0, 7),
(9, 'backOffice1', '*34FE89252BE32A81639E0A2953BBB20AC9456EF1', 'back', 'office', '', '', '', 1, '2009-11-10 11:46:29', 115, 7),

-- --------------------------------------------------------

--
-- Struttura della tabella `__system_userwarnings`
--

CREATE TABLE IF NOT EXISTS `__system_userwarnings` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(256) NOT NULL,
  `body` text,
  `date` datetime NOT NULL,
  `type` varchar(5) NOT NULL,
  `id_user` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_user` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Dump dei dati per la tabella `__system_userwarnings`
--


--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `cittadino_pratica`
--
ALTER TABLE `cittadino_pratica`
  ADD CONSTRAINT `cittadino_pratica_ibfk_1` FOREIGN KEY (`id_pratica`) REFERENCES `pratica` (`id_pratica`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `cittadino_pratica_ibfk_2` FOREIGN KEY (`cod_cittadino`) REFERENCES `cittadino` (`cod_cittadino`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `pratica`
--
ALTER TABLE `pratica`
  ADD CONSTRAINT `pratica_ibfk_1` FOREIGN KEY (`tipologia_pratica`) REFERENCES `tipologia_pratica` (`tipologia`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `pratica_ibfk_2` FOREIGN KEY (`stato`) REFERENCES `statoPratica` (`stato`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `pratica_ibfk_3` FOREIGN KEY (`priorita`) REFERENCES `priorita` (`livello`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `pratica_ibfk_4` FOREIGN KEY (`assegnataA`) REFERENCES `settore` (`nome_settore`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `pratica_ibfk_5` FOREIGN KEY (`ci_sono_novita`) REFERENCES `new` (`new_check`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `pratica_settore`
--
ALTER TABLE `pratica_settore`
  ADD CONSTRAINT `new_fk_constraint` FOREIGN KEY (`id_pratica`) REFERENCES `pratica` (`id_pratica`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `pratica_settore_ibfk_1` FOREIGN KEY (`nome_settore`) REFERENCES `settore` (`nome_settore`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `sotto_pratica`
--
ALTER TABLE `sotto_pratica`
  ADD CONSTRAINT `sotto_pratica_ibfk_1` FOREIGN KEY (`pratica_id_pratica`) REFERENCES `pratica` (`id_pratica`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `tag_pratica`
--
ALTER TABLE `tag_pratica`
  ADD CONSTRAINT `tag_pratica_ibfk_1` FOREIGN KEY (`id_pratica`) REFERENCES `pratica` (`id_pratica`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `__system_field`
--
ALTER TABLE `__system_field`
  ADD CONSTRAINT `__system_field_ibfk_1` FOREIGN KEY (`id`) REFERENCES `__system_resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `__system_field_ibfk_2` FOREIGN KEY (`id_resultset`) REFERENCES `__system_resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `__system_field_ibfk_3` FOREIGN KEY (`id_grouping`) REFERENCES `__system_grouping` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `__system_fieldinpreference`
--
ALTER TABLE `__system_fieldinpreference`
  ADD CONSTRAINT `__system_fieldinpreference_ibfk_1` FOREIGN KEY (`id_headerpreference`) REFERENCES `__system_headerpreference` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `__system_fieldinpreference_ibfk_2` FOREIGN KEY (`id_field`) REFERENCES `__system_field` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `__system_management`
--
ALTER TABLE `__system_management`
  ADD CONSTRAINT `__system_management_ibfk_1` FOREIGN KEY (`id_group`) REFERENCES `__system_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `__system_management_ibfk_2` FOREIGN KEY (`id_resource`) REFERENCES `__system_resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `__system_notify`
--
ALTER TABLE `__system_notify`
  ADD CONSTRAINT `__system_notify_ibfk_1` FOREIGN KEY (`resultset_id`) REFERENCES `__system_resultset` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `__system_resultset`
--
ALTER TABLE `__system_resultset`
  ADD CONSTRAINT `__system_resultset_ibfk_1` FOREIGN KEY (`id`) REFERENCES `__system_resource` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `__system_user`
--
ALTER TABLE `__system_user`
  ADD CONSTRAINT `__system_user_ibfk_1` FOREIGN KEY (`id_group`) REFERENCES `__system_group` (`id`) ON DELETE SET NULL;
