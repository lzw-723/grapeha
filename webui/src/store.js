import { ref, watch } from "vue";
import { getStorage, setStorage } from "./storage";

const token = ref("");
const username = ref("");
// window.token = token;
token.value = getStorage("token");
username.value = getStorage("username");

watch(token, (value) => setStorage("token", value));
watch(username, (value) => setStorage("username", value));

function getToken() {
  // return window.token;
  return token;
}
function getUsername() {
  return username;
}
export { getToken, getUsername };
