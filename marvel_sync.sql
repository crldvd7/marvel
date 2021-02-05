-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Servidor: localhost
-- Tiempo de generación: 04-02-2021 a las 19:24:28
-- Versión del servidor: 5.5.24-log
-- Versión de PHP: 5.4.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `marvel_sync`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `character`
--

CREATE TABLE IF NOT EXISTS `character` (
  `id_character` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8_spanish2_ci DEFAULT NULL,
  `id_character_api` int(7) NOT NULL,
  `name_api` varchar(500) COLLATE utf8_spanish2_ci NOT NULL,
  PRIMARY KEY (`id_character`),
  UNIQUE KEY `id_character_api` (`id_character_api`),
  KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci COMMENT='Characteres catalog' AUTO_INCREMENT=4 ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comic`
--

CREATE TABLE IF NOT EXISTS `comic` (
  `id_comic` int(11) NOT NULL AUTO_INCREMENT,
  `id_comic_api` int(7) NOT NULL,
  `name_api` varchar(500) COLLATE utf8_spanish2_ci NOT NULL,
  PRIMARY KEY (`id_comic`),
  UNIQUE KEY `id_comic_api` (`id_comic_api`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci COMMENT='Comics catalog' AUTO_INCREMENT=3 ;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `creator`
--

CREATE TABLE IF NOT EXISTS `creator` (
  `id_creator` int(11) NOT NULL AUTO_INCREMENT,
  `id_creator_api` int(7) NOT NULL,
  `name_api` varchar(500) COLLATE utf8_spanish2_ci NOT NULL,
  PRIMARY KEY (`id_creator`),
  UNIQUE KEY `id_creator_api` (`id_creator_api`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci COMMENT='Creators catalog' AUTO_INCREMENT=6 ;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rel_comic_character`
--

CREATE TABLE IF NOT EXISTS `rel_comic_character` (
  `id_comic_character` int(11) NOT NULL AUTO_INCREMENT,
  `id_comic` int(11) NOT NULL,
  `id_character` int(11) NOT NULL,
  PRIMARY KEY (`id_comic_character`),
  KEY `id_comic` (`id_comic`,`id_character`),
  KEY `id_character` (`id_character`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci COMMENT='Relation between which characters are present inside a comic' AUTO_INCREMENT=4 ;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rel_comic_creator`
--

CREATE TABLE IF NOT EXISTS `rel_comic_creator` (
  `id_comic_creator` int(11) NOT NULL AUTO_INCREMENT,
  `id_comic` int(11) NOT NULL,
  `id_creator` int(11) NOT NULL,
  `id_role` int(11) NOT NULL,
  PRIMARY KEY (`id_comic_creator`),
  KEY `id_comic` (`id_comic`),
  KEY `id_creator` (`id_creator`),
  KEY `id_role` (`id_role`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci COMMENT='Relation between which creators made a work for the comic' AUTO_INCREMENT=7 ;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `role`
--

CREATE TABLE IF NOT EXISTS `role` (
  `id_role` int(11) NOT NULL AUTO_INCREMENT,
  `name_api` varchar(100) COLLATE utf8_spanish2_ci NOT NULL,
  PRIMARY KEY (`id_role`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci COMMENT='Roles catalog' AUTO_INCREMENT=4 ;

--
-- Volcado de datos para la tabla `role`
--

INSERT INTO `role` (`id_role`, `name_api`) VALUES
(1, 'editor'),
(2, 'writer'),
(3, 'colorist');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `time_sync`
--

CREATE TABLE IF NOT EXISTS `time_sync` (
  `sync_datetime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sync_datetime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci COMMENT='Date and time of the last synchronization';

--
-- Volcado de datos para la tabla `time_sync`
--

INSERT INTO `time_sync` (`sync_datetime`) VALUES
('2021-02-04 06:09:01');

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `rel_comic_character`
--
ALTER TABLE `rel_comic_character`
  ADD CONSTRAINT `rel_comic_character_ibfk_1` FOREIGN KEY (`id_comic`) REFERENCES `comic` (`id_comic`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `rel_comic_character_ibfk_2` FOREIGN KEY (`id_character`) REFERENCES `character` (`id_character`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `rel_comic_creator`
--
ALTER TABLE `rel_comic_creator`
  ADD CONSTRAINT `rel_comic_creator_ibfk_1` FOREIGN KEY (`id_comic`) REFERENCES `comic` (`id_comic`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `rel_comic_creator_ibfk_2` FOREIGN KEY (`id_creator`) REFERENCES `creator` (`id_creator`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `rel_comic_creator_ibfk_3` FOREIGN KEY (`id_role`) REFERENCES `role` (`id_role`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
