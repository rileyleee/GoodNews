import { Route, Routes } from "react-router-dom";
import "./App.css";
import { HOME, ADMIN, LOGIN, NOTFOUND } from "./pages/Pages";
import { ROUTES } from "./common/constants/Routes";

function App() {
  return (
    <Routes>
      <Route path={ROUTES.HOME} Component={HOME} />
      <Route path={ROUTES.ADMIN} Component={ADMIN} />
      <Route path={ROUTES.LOGIN} Component={LOGIN} />
      <Route path={ROUTES.NOTFOUND} Component={NOTFOUND} />
    </Routes>
  );
}

export default App;
