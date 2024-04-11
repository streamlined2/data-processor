<h1>Запуск програми</h1>
Метод <i><b>main</b></i> класу <i><b>com.streamlined.dataprocessor.Driver</b></i> отримує як параметри теку файлів даних і назву властивості основної сутності <i><b>Person</b></i>, для якої формуються статистичні дані. Наприклад, можливі значення назви властивості <i><b>eyeColor</b></i>, <i><b>hairColor</b></i>, <i><b>favoriteMeals</b></i> (містить перелік улюблених страв, розділених комами). <p>

<h1>Опис сутностей</h1>
<p>
Основна сутність <b><i>com.streamlined.dataprocessor.entity.Person</i></b> Фізична особа
        <p>Атрибути<p>
		<i> 
        <ul>
        <li>ім'я <b>name</b></li>
		<li>день народження <b>birthday</b></li>
		<li>стать <b>sex</b></li>
		<li>колір очей <b>eyeColor</b></li>
		<li>колір волосся <b>hairColor</b></li>
		<li>вага <b>weight</b></li>
		<li>зріст <b>height</b></li>
		<li>країна походження <b>countryOfOrigin</b></li>
		<li>країна громадянства <b>citizenship</b></li>
		<li>перелік улюблених страв <b>favoriteMeals</b> (строки, розділені комами)</li>
        </ul>
        </i>
        </p>
Додаткова сутність <b><i>com.streamlined.dataprocessor.entity.Country</i></b> Країна походження або країна громадянства особи
        <p>Атрибути<p>
		<i> 
        <ul>
        <li>назва <b>name</b></li>
		<li>континент розташування <b>continent</b></li>
		<li>столиця <b>capital</b></li>
		<li>населення <b>population</b></li>
		<li>площа <b>square</b></li>
        </ul>
        </i>
        </p>    
</p>

<h1>Зразки файлів даних та результату</h1>
наведені за посиланнями
<p><i>https://github.com/streamlined2/data-processor/tree/main/src/main/resources</i>
<p><i>https://github.com/streamlined2/data-processor/tree/main/src/main/resources/data</i>
<p>
<h1>Вимірювання часу парсингу для різної кількості потоків</h1>
<p>
Тестовий набір був попередньо створений за допомогою класу <b><i>com.streamlined.dataprocessor.datagenerator.PersonDataGenerator</b></i> із параметрами <i> <b>PERSON_COUNT</b></i>  (загальна кількість сутностей) 1_000_000, та <i><b>FILE_COUNT</b></i>  (кількість файлів) 100.<p>
Вимірювання виконане за допомогою тесту <b><i>com.streamlined.dataprocessor.parser.MultithreadParsePerformanceTest.measureParseTime</i></b>
<p>
<ol>
<li>  Number of threads 1, parsing duration 10566 msec</li>
<li>  Number of threads 1, parsing duration 8124 msec</li>
<li>  Number of threads 2, parsing duration 5235 msec</li>
<li>  Number of threads 4, parsing duration 3231 msec</li>
<li>  Number of threads 8, parsing duration 2840 msec</li>
</ol>
Тривалість парсингу для одного потоку виміряно двічі, бо другий і наступні результати менші через буферизацію на рівні операційної системи, тож перший результат завищений і може бути відкинутий.<p>
Збільшення кількості потоків вдвічі призводить до прискорення парсингу, але швидкість зростає повільніше, ніж вдвічі, через обмеження інших складових системи і втрати часу на координацію потоків.
</p>

Час парсингу значень однієї властивості сутності для такого ж набору даних за допомогою Jackson Streaming API (тест <b><i>com.streamlined.dataprocessor.parser.MultithreadParsePerformanceTest.measureStreamingParseTime</b></i>) значно менший, що свідчить про перевагу даного методу. Крім того, він потребує лише фіксований обсяг пам'яті буферу для збереження даних перед опрацюванням, без виділення пам'яті для створення повної колекції сутностей.
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