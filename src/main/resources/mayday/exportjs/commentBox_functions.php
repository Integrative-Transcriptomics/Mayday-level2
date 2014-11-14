<?php

function streamComments($name)
{
    
    if(file_exists($name))
    {
        $fp=fopen($name,'rb') ;
        while (!feof($fp))
        {
            $order = fgets($fp);
            echo $order;
        }
    }else {
        echo "No comments yet";
    }
    
}

function writeForm($name)
{
echo <<< END
<form action="commentBox_post.php" method="post" onsubmit="return checkEntries(this)">
<input type="hidden" name="file" value="$name"/>
Name <input type="text" name="name" size="30" maxlength="35" border="0"><br/>
Comment <textarea name="comm" cols="30" rows="4"></textarea><br/>
<input type="submit" value="Post Comment"/>
</form>
END;
}

function writeCommentBox($name)
{
    writeForm($name);
    streamComments($name);
}

function buildComment($name, $comment)
{
    $res="<div class=\"commentItem\">".
        "<div class=\"commentName\">$name</div>".
        "<div class=\"commentTime\">".date('H:i, jS F Y')."</div>".
        "<div class=\"commentComment\">$comment</div>".
        "</div>\n";
    return $res;
}

?>