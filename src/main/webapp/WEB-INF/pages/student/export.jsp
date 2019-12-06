<%--
  Created by IntelliJ IDEA.
  User: hyz
  Date: 2019-11-15
  Time: 10:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript">
        $(document).ready(function(){
            $('input[type=checkbox]').click(function(){
                $(this).attr('disabled','disabled');
                if($("input[name='test']:checked").length >= 3)
                {
                    $("input[name='test']").attr('disabled','disabled');
                }
            });
            $("#count").click(function(){
                $('input').live('click',function(){
                    alert($('input:checked').length);
                });
            })
        })
    </script>
</head>
<body>
<ul>
    <li><input type="checkbox" name="test" />看电视</li>
    <li><input type="checkbox" name="test" />看电影</li>
    <li><input type="checkbox" name="test" />上网</li>
    <li><input type="checkbox" name="test" />爬山</li>
    <li><input type="checkbox" name="test" />游乐场</li>
    <li><input type="checkbox" name="test" />逛街</li>
    <li><input type="checkbox" name="test" />聚会</li>
</ul>
<p>
    <input type="button" id="count" value="有多少CheckBox被选中了？" />
</body>
</html>
