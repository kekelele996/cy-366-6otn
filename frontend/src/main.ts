import { createApp } from "vue";

import Vant from "vant";
import "vant/lib/index.css";
import App from "./App.vue";
import "./styles/app.css";

const app = createApp(App);

app.use(Vant);
app.mount("#root");
