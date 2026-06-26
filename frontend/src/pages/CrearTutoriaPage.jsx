import { Book, Calendar, MapPin, PenBoxIcon, School2, Users2Icon, Video, VideoIcon } from "lucide-react";
import { useEffect, useState } from "react";
import { materiaStore } from "../store/materias.store";
import toast from "react-hot-toast";
import { tutoriaStore } from "../store/tutorias.store";

function CrearTutoriaPage() {
	const materiasTutor = materiaStore((state) => state.materiasTutor);
	const [seleccion, setSeleccion] = useState(null);
	const [materiaSeleccionada, setMateriaSeleccionada] = useState("");
	const [sedeSeleccionada, setSedeSeleccionada] = useState("");
	const [ubiOrLink, setUbiOrLink] = useState("");
	const [dataForm, setDataForm] = useState({
		nombre: "",
		descripcion: "",
		tutorId: null,
		materiaId: materiaSeleccionada,
		fecha: "",
		horaInicio: "",
		horaFin: "",
		modalidad: "",
		sede: "",
		ubicacion: "",
		linkVirtual: "",
		linkDrive: "",
		cupo: null,
	});

	useEffect(() => {
		const fetchMaterias = async () => {
			await materiaStore.getState().obtenerMateriasTutor();
		};

		fetchMaterias();
	}, []);

	const today = new Date().toISOString().split("T")[0];

	const handleSubmit = (event) => {
		event.preventDefault();
		if (!materiaSeleccionada) {
			toast.error("Debes seleccionar una materia.");
			return;
		}

		if (!seleccion) {
			toast.error("Debes seleccionar una modalidad.");
			return;
		}

		if (!dataForm.fecha || dataForm.fecha < today) {
			toast.error("La fecha debe ser hoy o una fecha futura.");
			return;
		}

		if (dataForm.linkDrive) {
			toast(
				"Atención: la plataforma no se hace responsable del contenido almacenado en la carpeta compartida. Asegúrate de respetar los derechos de autor y de tener permiso para compartir cualquier material.",
				{ icon: "⚠️" },
			);
		}

		const storedUser = localStorage.getItem("user");
		const user = storedUser ? JSON.parse(storedUser) : null;

		const modalidad = seleccion === "P" ? "PRESENCIAL" : seleccion === "V" ? "VIRTUAL" : "";
		const payload = {
			...dataForm,
			tutorId: user?.id ?? null,
			materiaId: parseInt(materiaSeleccionada),
			modalidad,
			ubicacion: seleccion === "P" ? ubiOrLink : "",
			linkVirtual: seleccion === "V" ? ubiOrLink : "",
			linkDrive: dataForm.linkDrive,
		};

		console.log("payload: ", payload);
		tutoriaStore.getState().crearTutoria(payload);
	};

	return (
		<div className="bg-[#F7F9FB] px-20 py-10 min-h-screen">
			<div>
				<h1 className="font-bold text-2xl">Nueva Tutoria</h1>
				<h3 className="text-slate-800 text-md">Crear nueva sesión de tutoría para tus compañeros</h3>
			</div>
			<div className="bg-white border mt-3 border-slate-200 px-10 py-5 rounded-lg w-full h-full">
				<div>
					<h3 className="font-semibold">Información de la Tutoría</h3>
					<h4 className="text-slate-500 text-sm">Completa los datos para crear una nueva sesión</h4>
				</div>
				<div className="mt-3">
					<form onSubmit={handleSubmit}>
						<div className="flex flex-row justify-between ">
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Materia</span>
								<div className="border px-2 rounded-md shadow-sm border-slate-300 w-100">
									<Book className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
									<select
										value={materiaSeleccionada}
										onChange={(event) => setMateriaSeleccionada(event.target.value)}
										className="font-medium border-slate-300 rounded-md w-full h-8.5 pl-8"
									>
										<option value="" disabled>
											Selecciona la materia a dar
										</option>
										{materiasTutor.map((mat) => (
											<option value={mat.id} key={mat.id}>
												{mat.nombre}
											</option>
										))}
									</select>
								</div>
							</div>
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Sede</span>
								<div className="border px-2 rounded-md shadow-sm border-slate-300 w-100">
									<School2 className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
									<select
										value={sedeSeleccionada}
										onChange={(event) => {
											(setSedeSeleccionada(event.target.value), setDataForm({ ...dataForm, sede: event.target.value }));
										}}
										className="font-medium border-slate-300 rounded-md w-full h-8.5 pl-8"
									>
										<option value="" disabled>
											Selecciona la sede correspondiente
										</option>
										<option value="JUNIN">JUNIN</option>
										<option value="PERGAMINO">PERGAMINO</option>
									</select>
								</div>
							</div>
							<div>
								<div className="flex flex-col relative mt-3 gap-1">
									<span className="font-semibold text-slate-800 text-md">Cupo Máximo de Alumnos</span>
									<Users2Icon className="absolute bottom-1.5 left-4 size-5 text-slate-500" />
									<input
										type="number"
										className="border shadow-sm font-medium border-slate-300 rounded-md w-100 h-8.5 pl-12"
										placeholder="Ingresa el cupo máximo"
										onChange={(e) => setDataForm({ ...dataForm, cupo: e.target.value })}
									/>
								</div>
							</div>
						</div>
						<div className="flex flex-col relative mt-3 gap-1">
							<span className="font-semibold text-slate-800 text-md">Titulo de la tutoria</span>
							<PenBoxIcon className="absolute bottom-1.5 left-4 size-5 text-slate-500" />
							<input
								type="text"
								className="border shadow-sm font-medium border-slate-300 rounded-md w-full h-8.5 pl-12"
								placeholder="Ej: Repaso para parcial de POO"
								onChange={(e) => setDataForm({ ...dataForm, nombre: e.target.value })}
							/>
						</div>
						<div className="flex flex-col relative mt-3 gap-1">
							<span className="font-semibold text-slate-800 text-md">Descripción</span>
							<textarea
								className="border shadow-sm h-15 max-h-20 font-medium border-slate-300 rounded-md w-full  px-3 py-2"
								maxLength={200}
								placeholder="Describe que temas se van a tratar, que deben traer los estudiantes, etc. (Máx 200 caracteres)"
								onChange={(e) => setDataForm({ ...dataForm, descripcion: e.target.value })}
							/>
						</div>
						<div className="grid grid-cols-3 gap-5">
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Fecha</span>
								<div className="border px-2 rounded-md shadow-sm border-slate-300">
									<Calendar className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
									<input
										type="date"
										min={today}
										className=" font-medium border-slate-300 rounded-md w-full h-8.5 pl-8"
										onChange={(e) => setDataForm({ ...dataForm, fecha: e.target.value })}
										required
									/>
								</div>
							</div>
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Hora Inicio</span>
								<div className="border px-2 rounded-md shadow-sm border-slate-300">
									<Calendar className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
									<input
										type="time"
										required
										className=" font-medium border-slate-300 rounded-md w-full h-8.5 pl-8"
										onChange={(e) => setDataForm({ ...dataForm, horaInicio: e.target.value })}
									/>
								</div>
							</div>
							<div className="flex flex-col relative mt-3 gap-1">
								<span className="font-semibold text-slate-800 text-md">Hora Fin</span>
								<div className="border px-2 rounded-md shadow-sm border-slate-300">
									<Calendar className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
									<input
										type="time"
										className=" font-medium border-slate-300 rounded-md w-full h-8.5 pl-8"
										onChange={(e) => setDataForm({ ...dataForm, horaFin: e.target.value })}
										required
									/>
								</div>
							</div>
						</div>
						<div className="mt-4">
							<span className="font-semibold text-slate-800 text-md">Modalidad</span>
							<div className="flex flex-row gap-3 ">
								<div
									className={`w-[50%] ${seleccion === "P" ? "bg-[#008BBA] text-white" : "bg-slate-100"}  h-25 border hover:bg-[#008BBA] hover:text-white cursor-pointer transition-colors flex  border-slate-300 rounded-md shadow-sm flex-col justify-center items-center`}
									onClick={() => setSeleccion("P")}
								>
									<MapPin />
									<span>Presencial</span>
								</div>
								<div
									className={`w-[50%] ${seleccion === "V" ? "bg-[#008BBA] text-white" : "bg-slate-100"}  h-25 border hover:bg-[#008BBA] hover:text-white cursor-pointer transition-colors border-slate-300 rounded-md shadow-sm flex flex-col justify-center items-center`}
									onClick={() => setSeleccion("V")}
								>
									<Video />
									<span>Virtual</span>
								</div>
							</div>
						</div>
						<div className={`${seleccion === null ? "hidden" : "flex flex-col"}  relative mt-3 gap-1`}>
							<span className="font-semibold text-slate-800 text-md">{seleccion === "P" ? "Ubiación" : "Link sala Meet"}</span>
							<div className="border px-2 rounded-md shadow-sm border-slate-300">
								{seleccion === "P" ? (
									<MapPin className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
								) : (
									<VideoIcon className="absolute bottom-2 left-2.5 size-5 text-slate-500" />
								)}
								<input
									type="text"
									className=" font-medium border-slate-300 rounded-md w-full h-8.5 pl-8"
									placeholder={seleccion === "P" ? "Ej: Edificio Rivadavia Salon 1" : "Ingrese su sala de Google Meet"}
									onChange={(e) => setUbiOrLink(e.target.value)}
									required
								/>
							</div>
						</div>
						<div className="flex flex-col relative mt-4 gap-1">
							<span className="font-semibold text-slate-800 text-md">Carpeta compartida de Drive (opcional)</span>
							<input
								type="url"
								className="border shadow-sm font-medium border-slate-300 rounded-md w-full h-8.5 pl-4"
								placeholder="https://drive.google.com/drive/folders/..."
								value={dataForm.linkDrive}
								onChange={(e) => setDataForm({ ...dataForm, linkDrive: e.target.value })}
							/>
							<p className="text-sm text-slate-500 mt-1">
								La plataforma no se responsabiliza por el contenido ni por los derechos de autor del material almacenado en la
								carpeta compartida. Usa este enlace solo si tienes permiso para compartir los archivos y los contenidos cumplen con
								la normativa intelectual.
							</p>
						</div>
						<div>
							<button type="submit" className="bg-[#008BBA] text-white w-30 h-10 rounded-lg font-medium cursor-pointer mt-5">
								Crear Tutoría
							</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	);
}

export default CrearTutoriaPage;
