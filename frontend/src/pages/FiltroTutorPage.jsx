import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
	BookOpen,
	CalendarDays,
	ChevronDown,
	ChevronUp,
	GraduationCap,
	Home,
	ClipboardList,
	Search,
	Users,
} from "lucide-react";

import {
	Card,
	CardContent,
	CardDescription,
	CardHeader,
	CardTitle,
} from "@/components/ui/card";

import { Button } from "@/components/ui/button";

const EMAIL_EJEMPLO = "usuario.ejemplo@gmail.com";

function obtenerEmailDesdeToken(token) {
	if (!token) return EMAIL_EJEMPLO;

	const cleanToken = token.replace("Bearer ", "");
	const payload = cleanToken.split(".")[1];

	if (!payload) return EMAIL_EJEMPLO;

	try {
		const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
		const jsonPayload = atob(base64);
		const decoded = JSON.parse(jsonPayload);

		return decoded.sub || EMAIL_EJEMPLO;
	} catch {
		return EMAIL_EJEMPLO;
	}
}

const TUTORES = [
	{
		id: 1,
		apellido: "Pérez",
		nombre: "Juan",
		materia: "Bases de Datos",
		sede: "Junín",
		dias: ["Lunes", "Miércoles"],
		horario: "18:00 a 20:00",
		modalidad: "Presencial",
		contacto: "juan.perez@gmail.com",
		descripcion:
			"Tutor con disponibilidad para acompañar a estudiantes en consultas de prácticas y parciales.",
	},
	{
		id: 2,
		apellido: "Gómez",
		nombre: "Ana",
		materia: "Programación II",
		sede: "Pergamino",
		dias: ["Martes", "Jueves"],
		horario: "16:00 a 18:00",
		modalidad: "Virtual",
		contacto: "ana.gomez@gmail.com",
		descripcion:
			"Apoya en estructuras de datos, lógica de programación y resolución de ejercicios prácticos.",
	},
	{
		id: 3,
		apellido: "López",
		nombre: "Martín",
		materia: "Álgebra Lineal",
		sede: "Junín",
		dias: ["Viernes"],
		horario: "14:00 a 17:00",
		modalidad: "Presencial",
		contacto: "martin.lopez@gmail.com",
		descripcion:
			"Ideal para consultas sobre matrices, sistemas de ecuaciones y espacios vectoriales.",
	},
	{
		id: 4,
		apellido: "Fernández",
		nombre: "Lucía",
		materia: "Cálculo I",
		sede: "Rojas",
		dias: ["Lunes", "Viernes"],
		horario: "09:00 a 11:00",
		modalidad: "Híbrida",
		contacto: "lucia.fernandez@gmail.com",
		descripcion:
			"Acompaña el aprendizaje de límites, derivadas e integrales con material de apoyo.",
	},
];

export default function FiltroTutorPage() {
	const [busqueda, setBusqueda] = useState("");
	const [tutorExpandido, setTutorExpandido] = useState(null);
	const emailLogeado = obtenerEmailDesdeToken(localStorage.getItem("token"));

	const tutoresFiltrados = useMemo(() => {
		const termino = busqueda.trim().toLowerCase();

		if (!termino) return TUTORES;

		return TUTORES.filter((tutor) => {
			const textoBuscable = [
				tutor.apellido,
				tutor.nombre,
				`${tutor.apellido} ${tutor.nombre}`,
				tutor.materia,
				tutor.sede,
				tutor.modalidad,
				tutor.horario,
				tutor.dias.join(" "),
			]
				.join(" ")
				.toLowerCase();

			return textoBuscable.includes(termino);
		});
	}, [busqueda]);

	const toggleDetalles = (id) => {
		setTutorExpandido((actual) => (actual === id ? null : id));
	};

	return (
		<div className="min-h-screen bg-[#F7F9FB]">
			<header className="border-b border-slate-200 bg-[#F7F9FB]">
				<div className="mx-auto flex h-20 max-w-7xl items-center justify-between px-6">
					<div className="flex items-center gap-4">
						<img src="/logo-unnoba.png" alt="logo-unnoba" className="w-20 rounded-xl" />
						<h1 className="text-2xl font-bold text-slate-900">Tutorías</h1>
					</div>

					<nav className="flex items-center gap-2">
						<Link
							to="/"
							className="flex items-center gap-2 rounded-xl px-4 py-2 text-slate-700 hover:bg-slate-200"
						>
							<Home className="h-5 w-5" />
							Inicio
						</Link>

						<Link
							to="/tutorias"
							className="flex items-center gap-2 rounded-xl px-4 py-2 text-slate-700 hover:bg-slate-200"
						>
							<BookOpen className="h-5 w-5" />
							Tutorías
						</Link>

						<Link
							to="/mis-inscripciones"
							className="flex items-center gap-2 rounded-xl px-4 py-2 text-slate-700 hover:bg-slate-200"
						>
							<ClipboardList className="h-5 w-5" />
							Mis Inscripciones
						</Link>

						<Link
							to="/filtros-tutores"
							className="flex items-center gap-2 rounded-xl bg-slate-200 px-4 py-2 text-slate-900"
						>
							<GraduationCap className="h-5 w-5" />
							Filtros de Tutores
						</Link>
					</nav>

					<div className="flex items-center gap-3">
						<div className="flex h-10 w-10 items-center justify-center rounded-full bg-[#008BBA] font-semibold text-white">
							{emailLogeado.slice(0, 2).toUpperCase()}
						</div>

						<span className="font-medium text-slate-700">{emailLogeado}</span>
					</div>
				</div>
			</header>

			<div className="px-4 py-10">
				<div className="mx-auto flex w-full max-w-5xl flex-col gap-6">
					<div className="flex flex-col gap-2 text-center sm:text-left">
						<div className="mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-sky-100 sm:mx-0">
							<Users className="h-6 w-6 text-[#008BBA]" />
						</div>

						<h1 className="text-3xl font-bold text-slate-900">Filtros de Tutores</h1>
						<p className="text-sm text-slate-600">
							Buscá tutores por nombre, materia, sede, horario o días de atención y revisá sus detalles.
						</p>
					</div>

					<Card className="border-slate-200 shadow-md">
						<CardHeader className="pb-4">
							<CardTitle className="text-xl text-slate-900">Buscar tutorías disponibles</CardTitle>
							<CardDescription>
								Escribí cualquier dato para filtrar las tutorías según la materia o el tutor.
							</CardDescription>
						</CardHeader>

						<CardContent className="space-y-5 pt-0">
							<div className="relative">
								<Search className="pointer-events-none absolute left-4 top-1/2 h-5 w-5 -translate-y-1/2 text-slate-400" />
								<input
									value={busqueda}
									onChange={(e) => setBusqueda(e.target.value)}
									type="text"
									placeholder="Buscar por materia, tutor, sede, horario o día..."
									className="h-12 w-full rounded-xl border border-slate-300 bg-white pl-12 pr-4 text-slate-900 shadow-sm outline-none transition focus:border-[#008BBA] focus:ring-2 focus:ring-[#008BBA]/20"
								/>
							</div>

							<div className="flex items-center justify-between gap-3 rounded-xl bg-slate-50 px-4 py-3 text-sm text-slate-600">
								<span>
									{tutoresFiltrados.length} tutor{tutoresFiltrados.length === 1 ? "" : "es"} encontrado{tutoresFiltrados.length === 1 ? "" : "s"}
								</span>
								<span className="hidden sm:inline">Hacé click en "Ver detalles" para desplegar horario y días.</span>
							</div>

							<div className="grid gap-4">
								{tutoresFiltrados.length === 0 ? (
									<div className="rounded-xl border border-dashed border-slate-300 bg-white p-8 text-center text-slate-600">
										No se encontraron tutores con ese criterio de búsqueda.
									</div>
								) : (
									tutoresFiltrados.map((tutor) => {
										const abierto = tutorExpandido === tutor.id;

										return (
											<Card key={tutor.id} className="border-slate-200 shadow-sm">
												<CardHeader className="pb-3">
													<div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
														<div>
															<CardTitle className="text-xl text-slate-900">
																{tutor.apellido}, {tutor.nombre}
															</CardTitle>
															<CardDescription>{tutor.materia}</CardDescription>
														</div>

														<div className="text-left sm:text-right">
															<span className="inline-flex rounded-full bg-sky-100 px-3 py-1 text-xs font-semibold text-[#008BBA]">
																{tutor.modalidad}
															</span>
															<p className="mt-2 text-sm text-slate-600">{tutor.sede}</p>
														</div>
													</div>
												</CardHeader>

												<CardContent className="space-y-4 pt-0">
													<p className="text-sm text-slate-600">{tutor.descripcion}</p>

													<Button
														type="button"
														onClick={() => toggleDetalles(tutor.id)}
														className="bg-[#008BBA] text-white hover:bg-[#00779f]"
													>
														{abierto ? (
															<>
																<ChevronUp className="mr-2 h-4 w-4" />
																Ocultar detalles
															</>
														) : (
															<>
																<ChevronDown className="mr-2 h-4 w-4" />
																Ver detalles
															</>
														)}
													</Button>

													{abierto ? (
														<div className="grid gap-3 rounded-xl bg-slate-50 p-4 text-sm text-slate-700 sm:grid-cols-2">
															<div>
																<p className="font-semibold text-slate-900">Materia</p>
																<p>{tutor.materia}</p>
															</div>

															<div>
																<p className="font-semibold text-slate-900">Horario</p>
																<p>{tutor.horario}</p>
															</div>

															<div>
																<p className="font-semibold text-slate-900">Días</p>
																<div className="mt-1 flex flex-wrap gap-2">
																	{tutor.dias.map((dia) => (
																		<span key={dia} className="rounded-full bg-white px-3 py-1 text-xs font-medium text-slate-700 shadow-sm">
																			<CalendarDays className="mr-1 inline h-3.5 w-3.5 text-[#008BBA]" />
																			{dia}
																		</span>
																	))}
																</div>
															</div>

															<div>
																<p className="font-semibold text-slate-900">Contacto</p>
																<p>{tutor.contacto}</p>
															</div>
														</div>
													) : null}
												</CardContent>
											</Card>
										);
									})
								)}
							</div>
						</CardContent>
					</Card>
				</div>
			</div>
		</div>
	);
}
