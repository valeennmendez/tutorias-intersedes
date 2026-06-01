import { Routes, Route } from "react-router-dom";
import PruebaPage from "./pages/PruebaPage";

function App() {
	return (
		<Routes>
			<Route path="/prueba" element={<PruebaPage />} />
		</Routes>
	);
}

export default App;
