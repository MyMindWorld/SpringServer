0. Конфиги должны быть не в /conf/runners а в /config, logging.json нужно удалить. Парсеры из корень/src нужно перенести в /scripts/src
1. No value - если это была константа, то теперь ей можно выставить type const, если boolean - то "type":"boolean"
2. Скрипты из values параметров нужно перенести наверх, в values теперь только дефолтные значения
3. values должен быть списком ([ ]) а не обьектом ({ })
4. Проверить все пути в скриптах и скриптах для параметров, пути вида "./scripts/" необходимо заменить, сейчас все запускается из папки /scripts