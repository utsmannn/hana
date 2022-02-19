<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>${data.title}</title>
    <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/css/bootstrap.min.css'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <script src="https://unpkg.com/quicktype-playground@1" ></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script>
        <#include "src/bootstrap.js">
        <#include "src/script.js">
    </script>
    <style rel="stylesheet">
        <#include "src/style.css">
        #sidebar {
            background: #37474F;
        }
        #sidebar header {
            background-color: #263238;
        }
    </style>

</head>
<body>
<!-- partial:index.partial.html -->
<div id="viewport">
    <!-- Sidebar -->
    <div id="sidebar">
        <header>
            <a href="#up" id="title">${data.title}</a>
            <a id="subtitle">${data.author}</a>
        </header>
        ${data.sidebar}
    </div>
    <!-- Content -->
    <div id="content">
        <nav class="navbar navbar-default">
            <div class="container-fluid">
                <ul class="nav navbar-nav navbar-right" id="up">
                    ${data.topMenu}
                </ul>
            </div>
        </nav>
        <div class="container-fluid">
            ${data.preamble}
            ${data.content}
            ${data.objectData}
        </div>
    </div>
</div>
<!-- partial -->

</body>
</html>
