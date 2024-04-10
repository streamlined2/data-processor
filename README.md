<h1>Запуск програми</h1>
Метод <i><b>main</b></i> класу <i><b>com.streamlined.dataprocessor.Driver</b></i> отримує як параметри теку вихідних файлів і назву властивості основної сутності Person, для якої формуються статистичні дані.<p>

<h1>Опис сутностей</h1>
<p>
Основна сутність <b><i>com.streamlined.dataprocessor.entity.Person</i></b> Фізична особа
        <p>Атрибути:<i> ім'я, день народження, стать, колір очей та волосся, вага, зріст, країна походження та громадянства, перелік улюблених страв (строки, розділені комами)</i></p>
Додаткова сутність <b><i>com.streamlined.dataprocessor.entity.Country</i></b> Країна походження або країна громадянства особи
        <p>Атрибути:<i> назва, континент розташування, столиця, населення, площа</i></p>
    
</p>

<h1>Зразки файлів даних та результату</h1>
наведені за посиланням https://github.com/streamlined2/data-processor/tree/main/src/main/resources/data
<p>
<h1>Вимірювання часу парсингу для різної кількості потоків</h1>
<h2>виконане за допомогою тесту <i>com.streamlined.dataprocessor.parser.MultithreadParsePerformanceTest</i></h2>
<p>
Тестовий набір був попередньо згенерований за допомогою класу <b><i>com.streamlined.dataprocessor.datagenerator.PersonDataGenerator</b></i> із параметрами PERSON_COUNT (кількість сутностей) 1_000_000, та FILE_COUNT (кількість файлів) 100
<p>
<ol>
<li>  Number of threads 1, parsing duration 10566 msec</li>
<li>  Number of threads 1, parsing duration 8124 msec</li>
<li>  Number of threads 2, parsing duration 5235 msec</li>
<li>  Number of threads 4, parsing duration 3231 msec</li>
<li>  Number of threads 8, parsing duration 2840 msec</li>
</ol>
Тривалість парсингу для одного потоку виміряно двічі, бо другий і наступні результати менші через буферизацію на рівні операційної системи, тож перший результат завищений і має бути відкинутий.<p>
Збільшення кількості потоків вдвічі призводить до прискорення парсингу, але швидкість зростає повільніше, ніж вдвічі, через обмежені можливості інших складових системи і додатковий витрачений час для координації потоків.
</p>

Час часткового парсингу такого ж набору даних за допомогою Jackson Streaming API (клас <b><i>com.streamlined.dataprocessor.parser.StreamingParser</b></i> в тесті <b><i>com.streamlined.dataprocessor.parser.MultithreadParsePerformanceTest.measureStreamingParseTime</b></i>) значно менший, що свідчить про перевагу даного методу. Крім того, він потребує менше пам'яті.
<p>
<ol>
<li>  Number of threads 1, parsing duration 4671 msec via Streaming API</li>
<li>  Number of threads 1, parsing duration 4616 msec via Streaming API</li>
<li>  Number of threads 2, parsing duration 2461 msec via Streaming API</li>
<li>  Number of threads 4, parsing duration 1721 msec via Streaming API</li>
<li>  Number of threads 8, parsing duration 1247 msec via Streaming API</li>
</ol>
</p>
</p>