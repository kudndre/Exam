# POST-запрос: `<e:rs-post url="..." type="<optional>">`

Допустим имеется сервер, который в ответ на `POST` присылает в теле ответа ровно то же, что было в теле запроса

### [**Пример параметризованных кейсов**](- "response body check c:status=ExpectedToFail")

    <e:rs-post url="relative/url">
        <e:rs-case desc="Неверный ответ" variables="p1:p2" values="value of p1:value of p2,second variant for p1: second variant for p2">
            <body>
                {"exact": "${var.p1}", "template": 1}
            </body>
            <expected>
                {"exact": "${var.p2}", "template": "${json-unit.any-number}"}
            </expected>
        </e:rs-case>
    </e:rs-post>

<div>
    <e:rs-post url="relative/url">
        <e:rs-case desc="Неверный ответ" variables="p1:p2" values="value of p1:value of p2,second variant for p1: second variant for p2">
            <body>
                {"exact": "${var.p1}", "template": 1}
            </body>
            <expected>
                {"exact": "${var.p2}", "template": "${json-unit.any-number}"}
            </expected>
        </e:rs-case>
    </e:rs-post>
</div>