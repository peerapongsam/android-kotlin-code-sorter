# Android Kotlin Code Sorter Plugin

- Group and Sort code
- Only support kotlin

## Orders
- __Companion objects__ (`companion object {}`)

- __Properties and Fields__ (Sort by Visibility Modifiers and Alphabet)
    - Constants (`const val FOO = "foo"`)
    - Overriding Properties (`override val foo = "foo"`, `override var foo = "foo"`)
    - Read-only Properties(`val foo = "`)
    - Mutable Properties(`var foo = "foo"`)
    - Late-Initialized Properties (`lateinit var foo: String`)
    - Inject Properties (`val foo by inject<String>()` or `val foo by viewModel<AnyViewModel>()` or `@Inject lateinit var foo : String`)
    - Standard Delegated Properties (`val foo by lazy { "foo" }`)
    - Custom Delegated Properties (`val fooViewBinding by viewBinding(R.layout.activity_foo)`)
    - Backing Fields (`val foo get() = "foo"`, `var foo get() = "foo" set(value) { field = value }`)
    - Backing Properties (`private var _foo = "foo"`, `val foo = _foo`)
    - Other Properties

- __Secondary Constructor__ (`Foo() : this("foo")`)
    
- __Class Initializer__ (`init {}`)
    
- __Functions__
    - Activity, Fragment Lifecycle (`onAttach()`, `OnCreate()`, `onCreateView()`, `onViewCreated()`, `onActivityCreated()`, 
    `onViewStateRestored()`, `onRestart()`, `onResume()`, `onPause()`, `onStop()`, `onDestroyView()`, `onDestroy()`, `onDetach()`)
    - Override Functions
    - Unit Test (`@Before`, `@After`, `@Test`)
    - Functions (Sort by Visibility Modifiers and Alphabet)
    - EventBus Functions (`@Subscribe fun foo() {}`)
    
- __Classes and Interfaces__
    - Class (`class Foo()`) (Sort by Visibility Modifiers and Alphabet)
    - Inner Classes (`inner class Foo()`) (Sort by Visibility Modifiers and Alphabet)
    - Interfaces (`interface Foo`) (Sort by Visibility Modifiers and Alphabet)
     
- __Visibility Modifiers__
    - `public` or default (default is `public`)
    - `protected`
    - `internal`
    - `private`
    

## Inspired by
[Kotlin Code Sorter](https://plugins.jetbrains.com/plugin/11163-kotlin-code-sorter)
[Lifecycle Sorter](https://plugins.jetbrains.com/plugin/7742-lifecycle-sorter)