<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>Mybatis文件生成器</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <link href="//cdn.bootcss.com/spectre.css/0.2.11/spectre.min.css" rel="stylesheet">
    <link href="//cdn.bootcss.com/spectre.css/0.2.11/spectre-icons.min.css" rel="stylesheet">
    <link href="//cdn.bootcss.com/spectre.css/0.2.11/spectre-exp.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="/css/style.css">
    <link rel="shortcut icon" href="/img/favicon.ico">
</head>

<body>
<section class="section section-hero bg-gray">
    <div class="grid-hero columns container grid-960" style="margin: 0 auto;">
        <!--<header class="text-center"><h3>Oh-Mybatis</h3></header>-->
        <div class="column col-10 col-xs-12" style="margin: 0 auto;">
            <form class="form-horizontal" action="/gen" method="post" id="config-form">
                <div class="form-group">
                    <div class="col-5">
                        <label class="form-label">数据库连接</label>
                        <input class="form-input" type="text" placeholder="127.0.0.1" id="connection"
                               name="connection"
                               value="127.0.0.1" required/>
                    </div>
                    <div class="col-1">&nbsp;</div>
                    <div class="col-6">
                        <label class="form-label">数据库端口</label>
                        <input class="form-input" type="text" placeholder="3306" id="port" name="port"
                               value="3306" required/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-5">
                        <label class="form-label">数据库名</label>
                        <input class="form-input" type="text" placeholder="请输入数据库名" id="dataBase" name="dataBase"
                               required/>
                    </div>
                    <div class="col-1">&nbsp;</div>
                    <div class="col-6">
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-5">
                        <label class="form-label">用户名</label>
                        <input class="form-input" type="text" placeholder="请输入用户名" id="userId" name="userId"
                               required/>
                    </div>
                    <div class="col-1">&nbsp;</div>
                    <div class="col-6">
                        <label class="form-label">用户密码</label>
                        <input class="form-input" type="text" placeholder="请输入用户密码" id="userPass" name="userPass"
                               required/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-5">
                        <label class="form-label">实体所属包名</label>
                        <input class="form-input" type="text" placeholder="com.exmaple.entity" id="modelPath"
                               name="modelPath" required/>
                    </div>
                    <div class="col-1">&nbsp;</div>
                    <div class="col-6">
                        <label class="form-label">Mapper接口所属包名</label>
                        <input class="form-input" type="text" placeholder="com.exmaple.mapper" id="mapperPath"
                               name="mapperPath" required/>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-5">
                        <label class="form-label">MapperXml所属路径(classpath下)</label>
                        <input class="form-input" type="text" placeholder="mapper" id="mappingPath" name="mappingPath"
                               required/>
                    </div>
                    <div class="col-1">&nbsp;</div>
                    <div class="col-6">
                        <label class="form-label">Mybatis Mapper插件</label>
                        <input class="form-input" type="text" placeholder="输入Mapper基础接口，没有则不填" id="mapperPlugin"
                               name="mapperPlugin"/>
                    </div>
                </div>

                <div class="form-group" id="check-group">
                    <div class="col-5">
                        <input type="hidden"  name="isHump" value="1" id="isHump"/>
                        <input type="checkbox" checked onchange="changeCkbox(this, 'isHump')"/>是否生成驼峰式命名

                    </div>
                    <div class="col-1">&nbsp;</div>
                    <div class="col-6">
                        <input type="hidden"  name="isAlltable" value="1" id="isAlltable"/>
                        <input type="checkbox" checked onchange="changeCkbox(this, 'isAlltable')"/>是否生成所有表
                        <#--<label class="form-switch">-->
                            <#--<i class="form-icon"></i> -->
                        <#--</label>-->
                        <input id="add-item" class="btn btn-link" type="button" style="display: none" onclick="addItem()" value="添加一项" />
                        </input>
                    </div>
                </div>

                <div id="gen-btn" class="form-group float-right" style="margin-top: 10px;">
                    <button class="btn btn-primary" onclick="doSubmit();" type="button">生成并下载</button>
                </div>
            </form>
        </div>
    </div>
</section>

<div id="row-tpl" style="display: none;">
    <div class="form-group tab-items" rowid="0">
        <div class="col-5">
            <label class="form-label">数据库表名</label>
            <input class="form-input" name="tableNames" type="text" placeholder="请输入数据库表名" required/>
        </div>
        <div class="col-1">&nbsp;</div>
        <div class="col-5">
            <label class="form-label">实体名</label>
            <input class="form-input" name="modelNames" type="text" placeholder="请输入Java实体名" required/>
        </div>
        <div class="col-1">
            <label class="form-label">&nbsp;</label>
            <button class="btn btn-link" onclick="removeItem(this);">删除该项</button>
        </div>
    </div>
</div>

<script src="//cdn.bootcss.com/jquery/2.2.4/jquery.min.js"></script>
<script src="//cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<script src="//cdn.bootcss.com/jquery.form/4.2.1/jquery.form.min.js"></script>
<script src="//cdn.bootcss.com/jquery-validate/1.16.0/jquery.validate.min.js"></script>
<script src="/js/index.js"></script>
</body>
</html>