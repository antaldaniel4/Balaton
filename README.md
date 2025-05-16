

# **Fejlesztési dokumentáció – Balatoni látnivalók alkalmazás**

## 1.  **Projekt célja**

A Balaton alkalmazás célja, hogy interaktív térképen keresztül bemutassa a Balaton környéki látnivalókat. A felhasználó városokra kattintva információkat kaphat, kedvencek közé adhatja őket, vagy GPS segítségével megnézheti a látnivalótol mér távolságot. A menürendszer lehetővé teszi a különféle modulok közötti navigálást.

---

## 2.  **Alkalmazás felépítése**

### 2.1. **Főbb Java osztályok (Activity-k)**

| Osztály                                  | Funkció                                                               |
| ---------------------------------------- | --------------------------------------------------------------------- |
| `MainActivity`                            | Bejelntkezési lehetőségek megvalósítása                              |
| `MapActivity`                            | Térképes megjelenítés pöttyökkel a városok helyén, kattintható elemek |
| `LatnivaloActivity`                      | Látnivalók listázása kategória és város szerint                       |
| `RolamActivity`                          | Egyszerű bemutatkozó nézet a fejlesztőről                             |
| `GPSActivity`                            | A felhasználó helyének megjelenítése és közeli helyek kezelése        |


---

### 2.2. **Layout fájlok**

| Fájl                                              | Tartalom                                                              |
| ------------------------------------------------- | --------------------------------------------------------------------- |
| `activity_main.xml`                                | `constraintlayout` + `MaterialButton` + `TextInputLayout`|
| `activity_map.xml`                                | `CoordinatorLayout` + `MaterialToolbar` + `ImageView` + `FrameLayout` |
| `activity_latnivalo.xml`, `activity_gps.xml` stb. | Egyedi tartalmak + `DrawerLayout`                                     |
| `activity_rolam.xml`                              | Egyszerű bemutatkozó `TextView`                                       |


---

### 2.3. **Grafikai fájlok**

* **Menü**: `res/menu/drawer_menu.xml`, `top_app_bar_menu.xml`
* **Sztringek**: `res/values/strings.xml` → `navigation_drawer_open`, `navigation_drawer_close`, stb.
* **Drawable**: saját ikonok (pl. `heart`, `ic_logout`, `dot_background` stb.)

---

## 3.**Navigation Drawer funkció**

MapActivity rendelkezik:

* `DrawerLayout`
* `MaterialToolbar` (hamburger ikonnal)
* `NavigationView` menüvel

A `setNavigationItemSelectedListener(...)` metódus alapján a menüpontok az alábbi activity-ket indítják:

* Kezdőlap → `MapActivity`
* Látnivalók → `LatnivaloActivity`
* GPS → `GPSActivity`
* Rólam → `RolamActivity`
* Kijelentkezés → `LoginActivity`, + adatok törlése

---

## 4. **GPS alapú helymeghatározás**

* `ACCESS_FINE_LOCATION` permission szükséges
* `FusedLocationProviderClient` használatával a felhasználó pozíciója lekérdezhető
* Távolság `Location.distanceTo(...)` alapján számolható a látványosságok koordinátáihoz
* Közeli helyek kiemelése, értesítések, lista szűrés

---

## 5. **Kijelentkezés funkció**

* `SharedPreferences` törlése:

  ```java
  preferences.edit().clear().apply();
  ```
* `Intent.FLAG_ACTIVITY_CLEAR_TASK | NEW_TASK` → minden előző képernyő lezárása
* Átirányítás `LoginActivity`-be
