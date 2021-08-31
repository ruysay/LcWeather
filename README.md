

# LcWeather Native Android App

The project is a tempalte Android project which aims at providing real time weather status as well as 3 days and 15 days weather forcast in major cities in Australia, which applied MVVM pattern and integrateds open weather APIs to fetch current weather from the [OpenWeatherMap](https://openweathermap.org/api). Also,  Google APIs are also used to fetch places, cities, counties, coords etc. from [Google Map & Place API](https://developers.google.com/places/android-sdk/overview)). 

The main goal of this app is to be a sample of how to build an high quality Android application that uses the Architecture components, Dagger etc. in Kotlin.

# Features

### 1. Display the name of your city with its current temperature, weather condition, highest/lowest temperature, as well as the weather forecast for the upcoming 3 days.

### 2. Switch to the following 3 cities by swiping the screen to left or right. Sydney, Perth, and Hobart.

### 3. Display weather information for the next 15 days within one view.

                                                      
   
# Design
Below is an overview of app components in MVVM

    [View]  ----------------------> [ViewModel] -----------------------[Model]
      |     <----------------------     |      -----------------------   |
    MainActivity                   MainViewModel                    WeatherRepository
    LongForecastActivity 		   LongForecastViewModel            [ForecastWeatherRetrofitService] 
																    [CurrentWeatherRetrofitService]
																    [WeatherBitRetrofitService]

    


# Implementation Note
1. This app uses MvvM pattern to decouple View and business logic such as API calls. 
   MainActivity and LongForecastActivity observe changes of MainViewModel and LognForecastViewModel, they have no visibilty of how Model layers(WeatherRepository) works. The Views observe viewModel changes and handle the flow/UI accordingly.

2. An enum class is used to present screen states, it looks like below:

> enum class LoadStates {
> 	    START,
> 	    LOADING,
> 	    SUCCESS,
> 	    EMPTY,
> 	    ERROR
>     }

3. Current weather data is handled by CurrentWeatherRetrofitService in WeatherRepository which uses retrofit2 to convert HTTP API into an interface. The service provider is [OpenWeatherMap](https://openweathermap.org/api). 
4. 3 days weather forcast is handled by ForecastWeatherRetrofitService in WeatherRepository which uses retrofit2 to convert HTTP API into an interface. Though it consumes service provided by [OpenWeatherMap](https://openweathermap.org/api)  but the end points for current data and forecast data are from different URL, so 2 RetrofitService interfaces are created to implemente the APIs respectively.

5. Because of 15 days forecast are not available with free Open weather map API ID, so an alternative open weather API provided is essential to support 15 days forecast. WeatherBitRetrofitService is the key component to fulfill this fuction. It is observed that the data from Open weather map and Weather bit are different, using the same open weather API provider should be able to provide better UX in the future.

6. Mock web server is used in unit test cases to simulate response of the APIs to be integrated in the app.

# ToDos
1. Extract location relevant components in LocationWeatherFragment to a repository layer object to lift the complixity in LocationWeatherFragment and simplify the structure
2. Use DiffUtil to update only what is changed
3. Utilise single open weather API provider

