<!DOCTYPE html>
<html>
<head>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/extjs/6.0.0/classic/theme-classic/resources/theme-classic-all.css" rel="stylesheet" />
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/extjs/6.0.0/ext-all.js"></script>
    <script type="text/javascript">
        Ext.onReady(function() {
            var panel1 = {
                title : 'Panel1',
                //html : 'Panel1',
                id :'panel1',
                frame : true,
                anchor: '100%,75%'
            }

            var panel2 = {
                title : 'Panel2',
                //html : 'Panel2',
                id :'panel2',
                anchor: '100%,25%',
                frame : true

            }

            var myWin = new Ext.Window({
                id :'window',
                height : 400,
                width : 400,
                layout:'anchor',
                border :true,
                items : [
                    {
                        xtype:'textfield',
                        fieldLabel:'FIO'
                    },
                    {
                        xtype:'image',
                        width: 400,
                        height: 400,
                        src:'img/090329.JPG'
                    }

                ],
                bbar :[
                    "->",
                    {
                        text:'Match scan'
                    },"-",
                    {
                        text:'Close',
                        handler:function (self) {
                            myWin.close();
                        }
                    }
                ]
            });
            myWin.show();
        });
    </script>
</head>
<body>
<div id="helloWorldPanel"></div>
</body>
</html>