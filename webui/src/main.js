import App from "./App.vue";
import {createApp} from "vue";
import "@shoelace-style/shoelace/dist/themes/light.css";
import "@shoelace-style/shoelace/dist/themes/dark.css";
import "./style.css";

import {router} from "./routers";

createApp(App).use(router).mount("#app");
