<?php

foreach ($_GET as $key => $value) { eval("\$" . $key . " = \"" . $value . "\";");}

$firstname = $_REQUEST["firstname"];
$lastname = $_REQUEST["lastname"];
$username = $_REQUEST["username"];
$fpassword = $_REQUEST["fpassword"];
$location = $_REQUEST["location"];
$ranking = $_REQUEST["ranking"];
$membersince = $_REQUEST["membersince"];
$lastvisited = $_REQUEST["lastvisited"];

if($code == "54m3xuzm97z30rdfsloegjizvzgga12bshptv59o")
{
	require_once('db.inc.php');

	$insertquery="INSERT INTO expert_profile(first_name, last_name, user_name, fpassword, location, ranking, member_since, last_activity) VALUES ('$firstname', '$lastname', '$username', '$fpassword', '$location', $ranking, '$membersince', '$lastvisited')";
	$result = mysql_query($insertquery);

	mysql_close();
}

?>
