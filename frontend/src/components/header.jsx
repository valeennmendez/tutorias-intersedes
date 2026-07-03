"use client";

import { Link, useNavigate, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import logo from "../assets/img/unnoba-logo.png";
import { Button, buttonVariants } from "@/components/ui/button";
import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuItem,
	DropdownMenuLabel,
	DropdownMenuSeparator,
	DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Home, BookOpen, Calendar, User, LogOut, Menu, GraduationCap, ClipboardList, Bell, BellRing, Loader2 } from "lucide-react";
import { cn } from "@/lib/utils";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { useAuthStore } from "../store/auth.store";
import { useAvisosStore } from "../store/avisos.store";
import { formatDistanceToNow } from "date-fns";
import { es } from "date-fns/locale";

export function DashboardHeader({ profile }) {
	const navigate = useNavigate();
	const location = useLocation();
	const { logout } = useAuthStore();
	const pathname = location.pathname;
	const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

	const handleLogout = async () => {
		logout();
		navigate("/");
	};

	const getInitials = (nombre, apellido) => {
		return `${nombre.charAt(0)}${apellido.charAt(0)}`.toUpperCase();
	};

	const getRoleLabel = (role) => {
		switch (role) {
			case "admin":
				return "Administrador";
			case "tutor":
				return "Tutor";
			default:
				return "Alumno";
		}
	};

	const { notificaciones, notificacionesLoading, notificacionesLeidas, notificacionesVersion, fetchNotificaciones, marcarLeidas } =
		useAvisosStore();

	const notifsOrdenadas = [...notificaciones].sort((a, b) => new Date(b.fechaCreacion) - new Date(a.fechaCreacion));
	const noLeidas = notificaciones.filter((n) => !notificacionesLeidas.includes(n.id));
	const noLeidasCount = noLeidas.length;

	useEffect(() => {
		fetchNotificaciones();
	}, [fetchNotificaciones, notificacionesVersion]);

	const navItems = [
		{ href: "/dashboard", label: "Inicio", icon: Home },
		{ href: "/dashboard/tutorias", label: "Tutorías", icon: BookOpen },
	];

	if (profile.role === "tutor" || profile.role === "admin") {
		navItems.push({ href: "/dashboard/mis-inscripciones", label: "Mis Inscripciones", icon: Calendar });
		navItems.push({ href: "/dashboard/panel-tutor", label: "Panel Tutor", icon: GraduationCap });
		navItems.push({ href: "/dashboard/gestionar-avisos", label: "Gestionar Avisos", icon: Bell });
	}

	if (profile.role === "alumno") {
		navItems.push({ href: "/dashboard/mis-inscripciones", label: "Mis Inscripciones", icon: Calendar });
		navItems.push({ href: "/dashboard/bandeja-avisos", label: "Bandeja de Avisos", icon: Bell });
		navItems.push({ href: "/postulacion-tutor", label: "Ser Tutor", icon: ClipboardList });
	}

	return (
		<header className="sticky top-0 z-50 border-b border-slate-200 bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
			<div className="container mx-auto flex h-16 items-center justify-between px-4">
				<Link to="/dashboard" className="flex items-center gap-3">
					<img src={logo} alt="UNNOBA Logo" width={120} height={60} className="h-10 w-auto" />
					<span className="hidden font-semibold lg:inline-block text-foreground">Tutorías</span>
				</Link>

				{profile.role !== "admin" && (
					<nav className="hidden md:flex items-center gap-1">
						{navItems.map((item) => {
							const isActive = pathname === item.href || (item.href !== "/dashboard" && pathname.startsWith(item.href + "/"));
							return (
								<Link
									key={item.href}
									to={item.href}
									className={cn(
										buttonVariants({ variant: isActive ? "secondary" : "ghost", size: "sm" }),
										"inline-flex items-center gap-2",
										isActive ? "bg-sky-100 text-sky-900 hover:bg-sky-200" : "hover:bg-sky-100 hover:text-sky-900",
									)}
								>
									<item.icon className="h-4 w-4" />
									{item.label}
								</Link>
							);
						})}
					</nav>
				)}

				<div className="flex items-center gap-2">
					{/* Notification Bell */}
					<DropdownMenu
						onOpenChange={(open) => {
							if (open) marcarLeidas(notificaciones.map((n) => n.id));
						}}
					>
						<DropdownMenuTrigger asChild>
							<Button variant="ghost" size="icon" className="relative">
								{notificacionesLoading ? (
									<Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
								) : noLeidasCount > 0 ? (
									<BellRing className="h-5 w-5 text-amber-500" />
								) : (
									<Bell className="h-5 w-5 text-muted-foreground" />
								)}
								{noLeidasCount > 0 && (
									<span className="absolute -top-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white">
										{noLeidasCount > 9 ? "9+" : noLeidasCount}
									</span>
								)}
							</Button>
						</DropdownMenuTrigger>
						<DropdownMenuContent align="end" className="w-80">
							<DropdownMenuLabel>
								<div className="flex items-center justify-between">
									<span className="text-sm font-medium">Notificaciones</span>
									<BellRing className="h-4 w-4 text-muted-foreground" />
								</div>
							</DropdownMenuLabel>
							<DropdownMenuSeparator />
							{notificaciones.length === 0 ? (
								<div className="px-2 py-6 text-center text-sm text-muted-foreground">
									<Bell className="mx-auto h-8 w-8 mb-2 opacity-50" />
									No hay notificaciones
								</div>
							) : (
								<>
									<div className="max-h-72 overflow-y-auto">
										{notifsOrdenadas.slice(0, 5).map((aviso) => {
											const leido = notificacionesLeidas.includes(aviso.id);
											return (
												<DropdownMenuItem key={aviso.id} asChild className="cursor-pointer">
													<Link to="/dashboard/bandeja-avisos" className="flex flex-col items-start gap-1 px-3 py-2 relative">
														{!leido && <span className="absolute left-1 top-3 h-2 w-2 rounded-full bg-blue-500" />}
														<span className={"text-sm font-medium leading-tight" + (!leido ? " ml-3" : "")}>{aviso.titulo}</span>
														<span className={"text-xs text-muted-foreground line-clamp-1" + (!leido ? " ml-3" : "")}>
															{aviso.nombreTutoria}
														</span>
														<span className={"text-[10px] text-muted-foreground/70" + (!leido ? " ml-3" : "")}>
															{formatDistanceToNow(new Date(aviso.fechaCreacion), { addSuffix: true, locale: es })}
														</span>
													</Link>
												</DropdownMenuItem>
											);
										})}
									</div>
									<DropdownMenuSeparator />
									<DropdownMenuItem asChild>
										<Link to="/dashboard/bandeja-avisos" className="justify-center text-sm font-medium text-primary cursor-pointer">
											Ver todos los avisos
										</Link>
									</DropdownMenuItem>
								</>
							)}
						</DropdownMenuContent>
					</DropdownMenu>

					<DropdownMenu>
						<DropdownMenuTrigger asChild>
							<Button variant="ghost" className="gap-2 px-2">
								<Avatar className="h-8 w-8">
									<AvatarFallback className="bg-primary text-primary-foreground text-sm">
										{getInitials(profile.nombre, profile.apellido)}
									</AvatarFallback>
								</Avatar>
								<span className="hidden sm:inline-block text-sm font-medium">{profile.nombre}</span>
							</Button>
						</DropdownMenuTrigger>
						<DropdownMenuContent align="end" className="w-56">
							<DropdownMenuLabel>
								<div className="flex flex-col space-y-1">
									<p className="text-sm font-medium">
										{profile.nombre} {profile.apellido}
									</p>
									<p className="text-xs text-muted-foreground">{profile.email}</p>
									<span className="inline-flex items-center rounded-full bg-primary/10 px-2 py-0.5 text-xs font-medium text-primary w-fit mt-1">
										{getRoleLabel(profile.role)}
									</span>
								</div>
							</DropdownMenuLabel>
							<DropdownMenuSeparator />
							<DropdownMenuItem asChild>
								<Link to="/dashboard/perfil" className="cursor-pointer">
									<User className="mr-2 h-4 w-4" />
									Mi Perfil
								</Link>
							</DropdownMenuItem>
							<DropdownMenuSeparator />
							<DropdownMenuItem onClick={handleLogout} className="text-destructive cursor-pointer">
								<LogOut className="mr-2 h-4 w-4" />
								Cerrar Sesión
							</DropdownMenuItem>
						</DropdownMenuContent>
					</DropdownMenu>
					{profile.role !== "admin" && (
						<Sheet open={mobileMenuOpen} onOpenChange={setMobileMenuOpen}>
							<SheetTrigger asChild className="md:hidden">
								<Button variant="ghost" size="icon">
									<Menu className="h-5 w-5" />
									<span className="sr-only">Abrir menú</span>
								</Button>
							</SheetTrigger>
							<SheetContent side="right" className="w-64">
								<nav className="flex flex-col gap-2 mt-8">
									{navItems.map((item) => {
										const isActive = pathname === item.href || (item.href !== "/dashboard" && pathname.startsWith(item.href + "/"));
										return (
											<Link
												key={item.href}
												to={item.href}
												onClick={() => setMobileMenuOpen(false)}
												className={cn(
													buttonVariants({ variant: isActive ? "secondary" : "ghost" }),
													"justify-start inline-flex items-center gap-3",
													isActive ? "bg-sky-100 text-sky-900" : "hover:bg-sky-100 hover:text-sky-900",
												)}
											>
												<item.icon className="h-4 w-4" />
												{item.label}
											</Link>
										);
									})}
								</nav>
							</SheetContent>
						</Sheet>
					)}
				</div>
			</div>
		</header>
	);
}
