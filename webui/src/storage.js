function getStorage(name) {
  return localStorage.getItem(name);
}
function setStorage(name, value) {
  return localStorage.setItem(name, value);
}

export { getStorage, setStorage };
