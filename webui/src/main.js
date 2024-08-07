import App from "./App.vue";
import Varlet from "@varlet/ui";
import {createApp} from "vue";
import "./style.css";

import {router} from "./routers";

createApp(App).use(router).use(Varlet).mount("#app");
