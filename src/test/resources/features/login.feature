#language: es

@login @regresion
Característica: Login de usuario

  @login @regresion @success
  Escenario: Iniciar sesión con credenciales válidas
    Dado que el usuario está en la página de inicio de sesión
    Cuando el usuario ingresa su nombre de usuario y contraseña válidos
    Entonces el usuario debería ser redirigido a la página de inicio

  @login @regresion @error
  Escenario: Iniciar sesión con credenciales inválidas
    Dado que el usuario está en la página de inicio de sesión
    Cuando el usuario ingresa un nombre de usuario o contraseña inválidos
    Entonces se muestra un mensaje de error indicando que las credenciales son incorrectas

  @login @regresion @error
  Escenario: Iniciar sesión con campos vacíos
    Dado que el usuario está en la página de inicio de sesión
    Cuando el usuario deja los campos de nombre de usuario y contraseña vacíos
    Entonces se muestra un mensaje indicando que los campos son obligatorios