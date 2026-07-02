import { ArrowLeft, Book, Calendar, MapPin, PenBoxIcon, School2, Users2Icon, Video, VideoIcon } from "lucide-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
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
		<div className="min-h-screen bg-[#F7F9FB] px-4 py-10 sm:px-6 lg:px-8">
			<div className="mx-auto flex w-full max-w-5xl flex-col gap-6">
				<div className="flex items-center justify-between gap-4">
					<div>
						<Link to="/dashboard/panel-tutor" className="mb-3 inline-flex items-center gap-2 text-sm font-medium text-sky-700 hover:text-sky-800">
							<ArrowLeft className="h-4 w-4" />
							Volver al panel
						</Link>
						<h1 className="text-2xl font-bold text-slate-900">Nueva Tutoría</h1>
						<p className="text-sm text-slate-500">Crear nueva sesión de tutoría para tus compañeros</p>
					</div>
				</div>

				<div className="rounded-2xl border border-slate-200 bg-white shadow-md">
					<div className="border-b border-slate-200 px-6 py-5">
						<h3 className="text-lg font-semibold text-slate-900">Información de la Tutoría</h3>
						<h4 className="text-sm text-slate-500">Completa los datos para crear una nueva sesión</h4>
					</div>
					<div className="px-6 py-6">
						<form onSubmit={handleSubmit} className="space-y-5">
							<div className="flex flex-col gap-1">
								<span className="font-semibold text-slate-800 text-md">Materia</span>
								<div className="relative rounded-md border border-slate-300 shadow-sm">
									<Book className="pointer-events-none absolute left-2.5 top-1/2 size-5 -translate-y-1/2 text-slate-500" />
									<select
										value={materiaSeleccionada}
										onChange={(event) => setMateriaSeleccionada(event.target.value)}
										className="h-10 w-full rounded-md border-0 bg-transparent pl-9 pr-3 font-medium text-slate-900 outline-none"
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

							<div className="grid gap-4 lg:grid-cols-[1fr_0.8fr]">
								<div className="flex flex-col gap-1">
									<span className="font-semibold text-slate-800 text-md">Sede</span>
									<div className="relative rounded-md border border-slate-300 shadow-sm">
										<School2 className="pointer-events-none absolute left-2.5 top-1/2 size-5 -translate-y-1/2 text-slate-500" />
										<select
											value={sedeSeleccionada}
											onChange={(event) => {
												setSedeSeleccionada(event.target.value);
												setDataForm({ ...dataForm, sede: event.target.value });
											}}
											className="h-10 w-full rounded-md border-0 bg-transparent pl-9 pr-3 font-medium text-slate-900 outline-none"
										>
											<option value="" disabled>
												Selecciona la sede correspondiente
											</option>
											<option value="JUNIN">JUNIN</option>
											<option value="PERGAMINO">PERGAMINO</option>
										</select>
									</div>
								</div>

								<div className="flex flex-col gap-1">
									<span className="font-semibold text-slate-800 text-md">Cupo Máximo</span>
									<div className="relative rounded-md border border-slate-300 shadow-sm">
										<Users2Icon className="pointer-events-none absolute left-3 top-1/2 size-5 -translate-y-1/2 text-slate-500" />
										<input
											type="number"
											className="h-10 w-full rounded-md border-0 bg-transparent pl-10 pr-3 font-medium text-slate-900 outline-none"
											placeholder="Ingresa el cupo"
											onChange={(e) => setDataForm({ ...dataForm, cupo: e.target.value })}
										/>
									</div>
								</div>
							</div>

							<div className="flex flex-col gap-1">
								<span className="font-semibold text-slate-800 text-md">Título de la tutoría</span>
								<div className="relative rounded-md border border-slate-300 shadow-sm">
									<PenBoxIcon className="pointer-events-none absolute left-3 top-1/2 size-5 -translate-y-1/2 text-slate-500" />
									<input
										type="text"
										className="h-10 w-full rounded-md border-0 bg-transparent pl-10 pr-3 font-medium text-slate-900 outline-none"
										placeholder="Ej: Repaso para parcial de POO"
										onChange={(e) => setDataForm({ ...dataForm, nombre: e.target.value })}
									/>
								</div>
							</div>

							<div className="flex flex-col gap-1">
								<span className="font-semibold text-slate-800 text-md">Descripción</span>
								<textarea
									className="min-h-10 rounded-md border border-slate-300 px-3 py-2 font-medium text-slate-900 shadow-sm outline-none"
									maxLength={200}
									placeholder="Describe que temas se van a tratar, que deben traer los estudiantes, etc. (Máx 200 caracteres)"
									onChange={(e) => setDataForm({ ...dataForm, descripcion: e.target.value })}
								/>
							</div>

							<div className="grid gap-4 lg:grid-cols-3">
								<div className="flex flex-col gap-1">
									<span className="font-semibold text-slate-800 text-md">Fecha</span>
									<div className="relative rounded-md border border-slate-300 shadow-sm">
										<Calendar className="pointer-events-none absolute left-2.5 top-1/2 size-5 -translate-y-1/2 text-slate-500" />
										<input
											type="date"
											min={today}
											className="h-10 w-full rounded-md border-0 bg-transparent pl-9 pr-3 font-medium text-slate-900 outline-none"
											onChange={(e) => setDataForm({ ...dataForm, fecha: e.target.value })}
											required
										/>
									</div>
								</div>
								<div className="flex flex-col gap-1">
									<span className="font-semibold text-slate-800 text-md">Hora Inicio</span>
									<div className="relative rounded-md border border-slate-300 shadow-sm">
										<Calendar className="pointer-events-none absolute left-2.5 top-1/2 size-5 -translate-y-1/2 text-slate-500" />
										<input
											type="time"
											required
											className="h-10 w-full rounded-md border-0 bg-transparent pl-9 pr-3 font-medium text-slate-900 outline-none"
											onChange={(e) => setDataForm({ ...dataForm, horaInicio: e.target.value })}
										/>
									</div>
								</div>
								<div className="flex flex-col gap-1">
									<span className="font-semibold text-slate-800 text-md">Hora Fin</span>
									<div className="relative rounded-md border border-slate-300 shadow-sm">
										<Calendar className="pointer-events-none absolute left-2.5 top-1/2 size-5 -translate-y-1/2 text-slate-500" />
										<input
											type="time"
											className="h-10 w-full rounded-md border-0 bg-transparent pl-9 pr-3 font-medium text-slate-900 outline-none"
											onChange={(e) => setDataForm({ ...dataForm, horaFin: e.target.value })}
											required
										/>
									</div>
								</div>
							</div>

							<div>
								<span className="font-semibold text-slate-800 text-md">Modalidad</span>
								<div className="mt-2 flex flex-row gap-3">
									<div
										className={`w-[50%] ${seleccion === "P" ? "bg-[#008BBA] text-white" : "bg-slate-50"} h-24 border border-slate-300 hover:bg-[#008BBA] hover:text-white cursor-pointer transition-colors flex rounded-md shadow-sm flex-col justify-center items-center`}
										onClick={() => setSeleccion("P")}
									>
										<MapPin />
										<span>Presencial</span>
									</div>
									<div
										className={`w-[50%] ${seleccion === "V" ? "bg-[#008BBA] text-white" : "bg-slate-50"} h-24 border border-slate-300 hover:bg-[#008BBA] hover:text-white cursor-pointer transition-colors rounded-md shadow-sm flex flex-col justify-center items-center`}
										onClick={() => setSeleccion("V")}
									>
										<Video />
										<span>Virtual</span>
									</div>
								</div>
							</div>

							<div className={`${seleccion === null ? "hidden" : "flex flex-col"} relative gap-1`}>
								<span className="font-semibold text-slate-800 text-md">{seleccion === "P" ? "Ubicación" : "Link sala Meet"}</span>
								<div className="relative rounded-md border border-slate-300 shadow-sm">
									{seleccion === "P" ? (
										<MapPin className="pointer-events-none absolute left-2.5 top-1/2 size-5 -translate-y-1/2 text-slate-500" />
									) : (
										<VideoIcon className="pointer-events-none absolute left-2.5 top-1/2 size-5 -translate-y-1/2 text-slate-500" />
									)}
									<input
										type="text"
										className="h-10 w-full rounded-md border-0 bg-transparent pl-9 pr-3 font-medium text-slate-900 outline-none"
										placeholder={seleccion === "P" ? "Ej: Edificio Rivadavia Salon 1" : "Ingrese su sala de Google Meet"}
										onChange={(e) => setUbiOrLink(e.target.value)}
										required
									/>
								</div>
							</div>

							<div className="flex flex-col gap-1">
								<span className="font-semibold text-slate-800 text-md">Carpeta compartida de Drive</span>
								<input
									type="url"
									className="h-10 rounded-md border border-slate-300 px-3 font-medium text-slate-900 shadow-sm outline-none"
									placeholder="https://drive.google.com/drive/folders/..."
									value={dataForm.linkDrive}
									onChange={(e) => setDataForm({ ...dataForm, linkDrive: e.target.value })}
								/>
								<p className="text-sm text-slate-500">
									La plataforma no se responsabiliza por el contenido ni por los derechos de autor del material almacenado en la carpeta compartida.
									Usa este enlace solo si tienes permiso para compartir los archivos y los contenidos cumplen con la normativa intelectual.
								</p>
							</div>

							<div className="flex gap-3 pt-2">
								<button type="submit" className="rounded-lg bg-[#008BBA] px-5 py-2.5 font-medium text-white cursor-pointer hover:bg-[#00779f]">
									Crear Tutoría
								</button>
								<Link to="/dashboard/panel-tutor" className="rounded-lg border border-slate-300 px-5 py-2.5 font-medium text-slate-700 hover:bg-slate-50">
									Cancelar
								</Link>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	);
}

export default CrearTutoriaPage;
