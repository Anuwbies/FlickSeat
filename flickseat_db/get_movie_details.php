<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");

error_reporting(E_ALL);
ini_set('display_errors', 1);

include 'db_config.php';

$tmdb_id = $_POST['tmdb_id'] ?? '';
$movie_id = $_POST['movie_id'] ?? '';
	
if (empty($tmdb_id) && empty($movie_id)) {
    echo json_encode(["status" => "error", "message" => "Either tmdb_id or movie_id is required."]);
    exit;
}

if (!empty($tmdb_id)) {
    $sql = "SELECT * FROM movie WHERE tmdb_id = ?";
    $param = $tmdb_id;
} else {
    $sql = "SELECT * FROM movie WHERE movie_id = ?";
    $param = $movie_id;
}

$stmt = $conn->prepare($sql);
if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "Database prepare failed: " . $conn->error]);
    exit;
}

$stmt->bind_param("i", $param);
$stmt->execute();
$result = $stmt->get_result();

$movies = array();
if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $movies[] = $row;
    }
    echo json_encode(["status" => "success", "movies" => $movies]);
} else {
    echo json_encode(["status" => "error", "message" => "Movie not found."]);
}

$stmt->close();
$conn->close();
?>