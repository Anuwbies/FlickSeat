-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 25, 2025 at 05:53 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `flickseat_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `movie`
--

CREATE TABLE `movie` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `genre` text DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  `duration` varchar(10) DEFAULT NULL,
  `overview` text DEFAULT NULL,
  `rating` decimal(3,1) DEFAULT NULL,
  `tmdb_id` int(11) DEFAULT NULL,
  `status` enum('now showing','coming soon') NOT NULL DEFAULT 'coming soon'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `movie`
--

INSERT INTO `movie` (`id`, `title`, `genre`, `release_date`, `duration`, `overview`, `rating`, `tmdb_id`, `status`) VALUES
(1, 'Captain America: Brave New World', 'Action - Thriller - Science Fiction', '2025-02-14', '1h 59m', 'After meeting with newly elected U.S. President Thaddeus Ross, Sam finds himself in the middle of an international incident. He must discover the reason behind a nefarious global plot before the true mastermind has the entire world seeing red.', 6.2, 822119, 'now showing'),
(2, 'Companion', 'Science Fiction - Thriller', '2025-01-29', '1h 37m', 'During a weekend getaway at a secluded lakeside estate, a group of friends finds themselves entangled in a web of secrets, deception, and advanced technology. As tensions rise and loyalties are tested, they uncover unsettling truths about themselves and the world around them.', 7.0, 1084199, 'now showing'),
(3, 'Wicked', 'Drama - Romance - Fantasy', '2024-11-20', '2h 42m', 'In the land of Oz, ostracized and misunderstood green-skinned Elphaba is forced to share a room with the popular aristocrat Glinda at Shiz University, and the two\'s unlikely friendship is tested as they begin to fulfill their respective destinies as Glinda the Good and the Wicked Witch of the West.', 6.9, 402431, 'now showing'),
(4, 'Sonic the Hedgehog 3', 'Action - Science Fiction - Comedy - Family', '2025-01-15', '1h 50m', 'Sonic, Knuckles, and Tails reunite against a powerful new adversary, Shadow, a mysterious villain with powers unlike anything they have faced before. With their abilities outmatched in every way, Team Sonic must seek out an unlikely alliance in hopes of stopping Shadow and protecting the planet.', 7.8, 939243, 'now showing'),
(5, 'The Brutalist', 'Drama', '2025-03-05', '3h 35m', 'Escaping post-war Europe, visionary architect László Toth arrives in America to rebuild his life, his work, and his marriage to his wife Erzsébet after being forced apart during wartime by shifting borders and regimes. On his own in a strange new country, László settles in Pennsylvania, where the wealthy and prominent industrialist Harrison Lee Van Buren recognizes his talent for building. But power and legacy come at a heavy cost.', 7.0, 549509, 'coming soon'),
(6, 'Nosferatu', 'Horror - Fantasy', '2025-02-26', '2h 13m', 'A gothic tale of obsession between a haunted young woman and the terrifying vampire infatuated with her, causing untold horror in its wake.', 6.7, 426063, 'coming soon'),
(7, 'Flow', 'Animation - Fantasy - Adventure', '2025-03-05', '1h 25m', 'A solitary cat, displaced by a great flood, finds refuge on a boat with various species and must navigate the challenges of adapting to a transformed world together.', 8.3, 823219, 'coming soon'),
(8, 'The Seed of the Sacred Fig', 'Drama - Thriller - Crime', '2025-03-05', '2h 47m', 'Investigating judge Iman grapples with paranoia amid political unrest in Tehran. When his gun vanishes, he suspects his wife and daughters, imposing draconian measures that strain family ties as societal rules crumble.', 7.7, 1278263, 'coming soon'),
(9, 'Paddington in Peru', 'Family - Adventure - Comedy', '2025-01-29', '1h 46m', 'Paddington travels to Peru to visit his beloved Aunt Lucy, who now resides at the Home for Retired Bears. With the Brown Family in tow, a thrilling adventure ensues when a mystery plunges them into an unexpected journey through the Amazon rainforest and up to the mountain peaks of Peru.', 6.9, 516729, 'now showing');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `email`, `username`, `password`) VALUES
(1, 'asd', 'asd', 'asd'),
(2, 'qwe', 'qwe', '$2y$10$3UnZE9S6wJH1wX5joMTzYeZIYn8B8U4tTuUVh5Zd4mpgowJx6B4mu'),
(3, 'zxc', 'zxc', '$2y$10$YcwynZkQ8ej2awH103bTsOQ6omPcNYWfIICEoBx6PgLcYp8Aq1EmS'),
(4, 'email', 'asi', '$2y$10$1INQO0Vxl3hAd2/ahCwRVOPVnEVqL0XaqtEtD5csV79aOsUKDBU.u'),
(5, 'iop', 'iop', '$2y$10$lYeIbVBd3M1gEVhgWIlTv.YYvKy6yBa7GLPxh90csMEchUPgL6ZiC'),
(6, 'allen.email', 'allen', '$2y$10$MyhkvxL7UUSGAaoeDjlw4.5Er6cOyzUK8Bypep67FLTDl0jGlQgDW'),
(7, 'mimaw@email', 'mimaw', '$2y$10$PpsgJBAXj8r.Nf3S6pfRNecp5PR.HwZU5RjvCdpUrYxnpV5jOog.K'),
(8, 'fgh', 'fgh', '$2y$10$vzAqQsXYbxWCY008aDIIwOG4GfleN.wBn8zGVR1jpkinzNQSezOK2');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `movie`
--
ALTER TABLE `movie`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `tmdb_id` (`tmdb_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `movie`
--
ALTER TABLE `movie`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
