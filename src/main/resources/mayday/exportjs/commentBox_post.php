<?php

include('commentBox_functions.php');
$comm=$_POST['comm'];
$name=$_POST['name'];
$file=$_POST['file'];

$outputstring=buildComment($name, $comm);
$fp = fopen($file,'ab');
fwrite($fp,$outputstring);
fclose($fp);
header('Location: index.php');

?>