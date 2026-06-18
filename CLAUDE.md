# CLAUDE.md — Negociação Bezerros (Android · Java)

Native Android app for **cattle (bezerro) trading**: simulate/negotiate lot purchases using market quotes, freight, and broker commission. Stack: **MVVM + LiveData · Hilt · Room · Retrofit · Navigation Component · ViewBinding**. Architecture: **Clean Architecture** (Presentation → Domain → Data).

Pricing works in **arrobas (1 arroba = 30 kg)**. All money uses `BigDecimal` with scales from `Decimals` (`ESCALA_MONETARIA`, `ESCALA_CALCULO`, `ARREDONDAMENTO_PADRAO`).

---

## ⛔ Hard rules — never violate

1. **Fragment:** never call `getValue()` outside an observer. Consume LiveData only inside the observer scope.
2. **Fragment:** never store observable data in local/instance variables — use it directly in the observer.
3. **Fragment:** never put logic inside an observer lambda — delegate to a named method (`this::onUiStateChanged`).
4. **ViewModel:** no instance fields except `MutableLiveData`. Temporary state lives inside methods.
5. **ViewModel:** `MutableLiveData` is always `private`; expose an immutable `LiveData`. Never expose `MutableLiveData`.
6. **One UiState per screen.** Never post loose primitives; always post a `UiState`.
7. **Fail Fast:** no nested `if`, no `else` when an early `return` resolves it.
8. **One level of abstraction per method.** Never mix orchestration with implementation details.
9. **Every business/validation condition** becomes a boolean method with a semantic name.
10. **Navigation:** never call `findNavController()` directly — go through `NavigationHelper`. Always pass args via Safe Args.
11. Methods stay short (~15 lines max) and single-responsibility. Follow the naming conventions below exactly.

---

## Code style

**Single level of abstraction** — orchestration and detail never share a method:
```java
// ✅
private void loadUser() {
    showLoading();
    observeUserState();
}
private void onUserStateChanged(UserUiState state) {
    hideLoading();
    bindUserState(state);
}
```

**Fail Fast + boolean methods** — validate and return early; extract conditions:
```java
// ✅
private void processOrder(Order order) {
    if (isOrderInvalid(order)) return;
    if (isOrderEmpty(order)) return;
    submitOrder(order);
}
private boolean isOrderInvalid(Order order) { return order == null || !order.isValid(); }
private boolean isOrderEmpty(Order order)   { return !order.hasItems(); }

if (isUserEligible(user)) { ... }
private boolean isUserEligible(User user) {
    return user != null && user.getAge() >= 18 && user.isVerified();
}
```

---

## ViewModel

UiState models every screen state via static factories. State posting is extracted per state.
```java
public class UserUiState {
    public final boolean isLoading;
    public final User data;
    public final String errorMessage;
    private UserUiState(boolean l, User d, String e) { isLoading = l; data = d; errorMessage = e; }
    public static UserUiState loading()            { return new UserUiState(true, null, null); }
    public static UserUiState success(User data)   { return new UserUiState(false, data, null); }
    public static UserUiState error(String msg)    { return new UserUiState(false, null, msg); }
}

public class UserViewModel extends ViewModel {
    private final MutableLiveData<UserUiState> _uiState = new MutableLiveData<>();
    public final LiveData<UserUiState> uiState = _uiState;

    public void loadUser(String userId) {
        postLoadingState();
        fetchUserById(userId);
    }
    private void fetchUserById(String userId) {
        userRepository.getUser(userId, this::onUserFetched, this::onFetchError);
    }
    private void onUserFetched(User user)   { postSuccessState(user); }
    private void onFetchError(String msg)   { postErrorState(msg); }
    private void postLoadingState()         { _uiState.setValue(UserUiState.loading()); }
    private void postSuccessState(User u)   { _uiState.setValue(UserUiState.success(u)); }
    private void postErrorState(String m)   { _uiState.setValue(UserUiState.error(m)); }
}
```

---

## Fragment — standard structure

```java
public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    private UserViewModel viewModel;

    @Override public View onCreateView(LayoutInflater i, ViewGroup c, Bundle s) {
        binding = FragmentUserBinding.inflate(i, c, false);
        return binding.getRoot();
    }
    @Override public void onViewCreated(@NonNull View v, Bundle s) {
        super.onViewCreated(v, s);
        initViewModel();
        observeUiState();
        setupClickListeners();
    }
    private void initViewModel()   { viewModel = new ViewModelProvider(this).get(UserViewModel.class); }
    private void observeUiState()  { viewModel.uiState.observe(getViewLifecycleOwner(), this::onUiStateChanged); }

    private void onUiStateChanged(UserUiState state) {
        handleLoadingVisibility(state.isLoading);
        if (isDataReady(state)) bindUserData(state.data);
        if (hasError(state))    showErrorMessage(state.errorMessage);
    }
    private boolean isDataReady(UserUiState s) { return !s.isLoading && s.data != null; }
    private boolean hasError(UserUiState s)    { return s.errorMessage != null; }

    private void handleLoadingVisibility(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.contentGroup.setVisibility(loading ? View.GONE : View.VISIBLE);
    }
    private void bindUserData(User u) {
        binding.tvUserName.setText(u.getName());
        binding.tvUserEmail.setText(u.getEmail());
    }
    private void showErrorMessage(String m) { Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show(); }
    private void setupClickListeners()      { binding.btnConfirm.setOnClickListener(v -> onConfirmClicked()); }
    private void onConfirmClicked()         { viewModel.confirmUser(); }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
```

---

## Naming conventions

**Classes:** `<Feature>ViewModel`, `<Feature>Fragment`, `<Feature>Activity`, `<Feature>UiState`, `<Feature>Adapter`, `<Feature>Repository`. Private LiveData `_uiState`; public `uiState`.

**Layouts:** `fragment_<name>.xml`, `activity_<name>.xml`, `item_<name>.xml`, `dialog_<name>.xml` → binding `Fragment<Name>Binding`.

**View IDs:** `tv_` TextView · `et_` EditText · `btn_` Button · `iv_` ImageView · `rv_` RecyclerView · `pb_` ProgressBar · `cl_` ConstraintLayout · `ll_` LinearLayout · `cv_` CardView · `cb_` CheckBox · `rb_` RadioButton · `sw_` Switch.

**Methods by context:**
| Context | Verb / pattern | Example |
|---|---|---|
| Init | `init` / `setup` | `initViewModel()`, `setupClickListeners()` |
| Observe | `observe` | `observeUiState()` |
| State callback | `on<State>Changed` | `onUiStateChanged()` |
| Bind | `bind` | `bindUserData()` |
| Visibility | `handle…Visibility` / `show` / `hide` | `handleLoadingVisibility()` |
| Boolean | `is` / `has` / `can` / `should` | `isDataReady()`, `hasError()` |
| Fragment action | `on<Element><Action>` | `onConfirmClicked()` |
| ViewModel load | `load` / `fetch` | `loadUser()`, `fetchOrders()` |
| ViewModel business | imperative verb | `confirmUser()`, `deleteOrder()` |
| ViewModel post | `post<State>State` | `postSuccessState()` |

---

## Project structure

`app/src/main/java/com/omni/negociacaobezerros/`
```
data/
  models/                 Pure POJOs (PrecificacaoBezerro, Rota, Transporte…)
  repositories/           Logic over local DAOs
  source/
    local/                Room: AppDatabase, xgp_* entities, DAOs, Converters
    network/gespec/       Retrofit @Gespec (internal API)
    network/google/       Retrofit @GoogleMaps (Routes API)
domain/
  contract/               Strategy interfaces (PrecificacaoBezerroStrategy)
  usecase/                Use cases (PrecificarBezerroUseCase)
  strategy/               SemFrete, ComFrete, ComFreteEComissao
ui/
  fragments/layout/       Screen fragments (Home, Cotacao, Negociacao, Frete,
                          NegociacaoAnimal, Finalizacao, Conexao, Sincronizacao)
  fragments/sheet/        Bottom sheets (Categoria, Corretor, Empresa, Localizacao)
  fragments/dialog/       Standard dialogs
  viewmodels/  states/  adapters/  helpers/   (NavigationHelper, AlertHelper, PermissionHelper)
di/
  AppModule               SharedPreferences
  local/                  DataModule (DAOs), MapperModule, LocationModule, ExecutorModule
  network/gespec/         GespecNetworkModule + GespecServiceModule
  network/google/         GoogleMapsNetworkModule + GoogleMapsServiceModule
utils/
  document/pdf/           PdfGenerator + PdfBand hierarchy
  format/                 Decimals constants, parsing helpers
  mapper/                 MapStruct (@Mapper(componentModel = "inject"))
```
`res/navigation/navigation.xml` — start destination `homeFragment`.

---

## Architecture notes

**Navigation flow**
```
Home → Conexao → Sincronizacao
Home → Cotacao → Negociacao
Negociacao ↔ Frete                       (optional freight detour)
Negociacao → NegociacaoAnimal → Finalizacao
Negociacao → Finalizacao                 (direct, skips animal detail)
```
Use Safe Args (`NegociacaoFragmentDirections`) + `NavigationHelper` (wraps `findNavController()`, prevents double-navigation crashes).

**Strategy — pricing.** `PrecificacaoBezerroStrategy` → `…SemFrete` (base), `…ComFrete` (+ freight/km), `…ComFreteEComissao` (+ broker commission/kg). `PrecificarBezerroUseCase` injects the active strategy, reads latest `ValorReferencia`, builds `ParametrosBezerro`. Agio (premium) in `PrecificacaoBezerroRepository` iterates by arroba steps for animals below base weight.

**Room.** `AppDatabase` · file `"Sample.db"` · version `1`. All tables prefixed `xgp_`. Pre-seeded on `onCreate`: freight vehicle types/capacities (Truck, Carreta Baixa/Alta/Três Eixos), freight rate tables (flat ≤300 km, per-km beyond), reference types (CEPEA/Esalq, Mercado Local, Negociação Particular), default companies, broker, initial reference value, breeds.

**Network.** Two Retrofit instances by Hilt qualifier: `@Gespec` (base URL via interceptor reading SharedPreferences) and `@GoogleMaps` (`https://routes.googleapis.com/`, key via `GoogleMapsInterceptor` from build config). Keys in `secrets.properties` (not in VCS; names in `local.defaults.properties`; Secrets Gradle Plugin).

**Mappers.** Live in `utils/mapper/`, annotated `@Mapper(componentModel = "inject")`; register new bindings in `di/local/MapperModule` if needed.

**DI modules:** `AppModule` (SharedPreferences), `DataModule` (DAOs), `MapperModule`, `LocationModule`, `ExecutorModule`, `Gespec{Network,Service}Module`, `GoogleMaps{Network,Service}Module`.

---

## Build commands

```bash
./gradlew assembleDebug          # debug APK
./gradlew assembleRelease        # release APK
./gradlew test                   # unit tests
./gradlew connectedAndroidTest   # instrumented (needs device/emulator)
./gradlew test --tests "com.omni.negociacaobezerros.ClassName"
./gradlew clean assembleDebug
```
Windows: use `gradlew.bat`.

---

## Review checklist

- [ ] Single level of abstraction; method ≤ ~15 lines
- [ ] Fail Fast — no nested `if`, no avoidable `else`
- [ ] Conditions extracted to boolean methods
- [ ] ViewModel: only `MutableLiveData` fields; `MutableLiveData` private, `LiveData` public/immutable
- [ ] State via `UiState` factories
- [ ] Fragment: no `getValue()` outside observer; no local vars for LiveData; observers delegate to named methods
- [ ] Navigation via Safe Args + `NavigationHelper`
- [ ] Naming (class / method / ID) follows conventions