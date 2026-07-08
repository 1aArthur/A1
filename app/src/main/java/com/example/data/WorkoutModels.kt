package com.example.data

data class Exercise(
    val id: String,
    val name: String,
    val category: String, // PEITO, COSTAS, OMBROS, BRAÇOS, CORE, PERNAS, MOBILIDADE, CARDIO
    val difficulty: String, // INICIANTE, INTERMEDIÁRIO, AVANÇADO
    val equipmentNeeded: String, // "Nenhum", "Barra", "Argolas", "Elástico", "Halteres", "Banco/Cadeira", "Mochila"
    val defaultReps: String, // e.g. "4 séries x 10 reps" or "30 segundos"
    val description: String,
    val proTips: String,
    val animationPlaceholder: String // A simple string key representing the avatar movement
)

data class Workout(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val estimatedDurationMinutes: Int,
    val xpReward: Int,
    val goldReward: Int,
    val equipmentNeeded: String,
    val exercises: List<Exercise>
)

object WorkoutCatalog {
    val exercises = listOf(
        // PEITO
        Exercise(
            "pushup_standard",
            "Flexão Tradicional",
            "PEITO",
            "INICIANTE",
            "Nenhum",
            "4 séries x 12 reps",
            "Mantenha o corpo alinhado desde a cabeça até os calcanhares. Desça até o peito quase tocar o chão e empurre com força.",
            "Contraia o glúteo e o abdômen para proteger a lombar e maximizar a ativação do peitoral.",
            "pushup"
        ),
        Exercise(
            "pushup_decline",
            "Flexão Declinada",
            "PEITO",
            "INTERMEDIÁRIO",
            "Banco/Cadeira",
            "3 séries x 10 reps",
            "Apoie os pés em uma superfície elevada (banco ou cadeira) e as mãos no chão. Execute a flexão focando na porção superior do peito.",
            "Perfeito para simular o supino inclinado e construir ombros frontais fortes.",
            "pushup_decline"
        ),
        Exercise(
            "ring_dips",
            "Paralelas nas Argolas",
            "PEITO",
            "AVANÇADO",
            "Argolas",
            "4 séries x 8 reps",
            "Segure nas argolas e suspenda o corpo. Desça flexionando os cotovelos mantendo as argolas próximas ao corpo. Suba até estender totalmente.",
            "Exige extrema estabilidade e força dos ombros, peitoral e tríceps.",
            "dips"
        ),
        // COSTAS
        Exercise(
            "pullup_assisted",
            "Barra Fixa Escapular / Assistida",
            "COSTAS",
            "INICIANTE",
            "Elástico",
            "3 séries x 8 reps",
            "Com um elástico de resistência preso na barra, apoie os joelhos e execute a puxada focando na retração das escápulas.",
            "Excelente para construir a coordenação necessária para a barra livre.",
            "pullup"
        ),
        Exercise(
            "pullup_standard",
            "Barra Fixa Pronada",
            "COSTAS",
            "INTERMEDIÁRIO",
            "Barra",
            "4 séries x 6-10 reps",
            "Segure na barra com pegada pronada (palmas para frente) mais aberta que os ombros. Puxe o corpo até o queixo ultrapassar a barra.",
            "Evite usar o balanço do corpo. Foque em esmagar as dorsais.",
            "pullup"
        ),
        Exercise(
            "backpack_row",
            "Remada Curvada com Mochila",
            "COSTAS",
            "INICIANTE",
            "Mochila",
            "4 séries x 15 reps",
            "Encha uma mochila com livros ou garrafas de água. Incline o tronco para frente mantendo a coluna ereta e puxe a mochila em direção ao abdômen.",
            "Mantenha os cotovelos colados ao corpo para ativação máxima da grande dorsal.",
            "row"
        ),
        // PERNAS
        Exercise(
            "squat_standard",
            "Agachamento Livre",
            "PERNAS",
            "INICIANTE",
            "Nenhum",
            "4 séries x 20 reps",
            "Afaste os pés na largura dos ombros. Flexione os joelhos e empurre o quadril para trás como se fosse sentar em uma cadeira.",
            "Mantenha o peito aberto e não deixe os joelhos colapsarem para dentro.",
            "squat"
        ),
        Exercise(
            "pistol_squat",
            "Pistol Squat (Unilateral)",
            "PERNAS",
            "AVANÇADO",
            "Nenhum",
            "3 séries x 5 reps de cada lado",
            "Equilibre-se em uma perna só, estenda a outra perna à frente e agache o máximo que puder de forma controlada.",
            "Utilize uma cadeira ou poste para apoio caso falte equilíbrio no início.",
            "pistol"
        ),
        // BRAÇOS / TRÍCEPS / BÍCEPS
        Exercise(
            "bench_dips",
            "Mergulho no Banco",
            "BRAÇOS",
            "INICIANTE",
            "Banco/Cadeira",
            "3 séries x 15 reps",
            "Apoie as mãos na borda de um banco estável, pernas estendidas à frente. Flexione os cotovelos até 90 graus e suba espremendo o tríceps.",
            "Mantenha as costas bem próximas ao banco durante toda a descida.",
            "dips"
        ),
        Exercise(
            "chinup",
            "Barra Supinada (Chin-Up)",
            "BRAÇOS",
            "INTERMEDIÁRIO",
            "Barra",
            "4 séries x 8 reps",
            "Segure na barra com pegada supinada (palmas voltadas para você). Puxe-se para cima focando na contração intensa dos bíceps.",
            "Mantenha a descida lenta para rasgar as fibras musculares do bíceps.",
            "pullup"
        ),
        // OMBROS
        Exercise(
            "pike_pushup",
            "Pike Pushup (Flexão Pike)",
            "OMBROS",
            "INTERMEDIÁRIO",
            "Nenhum",
            "4 séries x 8 reps",
            "Fique em posição de V invertido com os quadris altos e as mãos no chão. Desça a cabeça diagonalmente em direção ao chão e empurre.",
            "É o melhor exercício de peso corporal para desenvolver ombros densos e força de empurrar vertical.",
            "pike"
        ),
        // ABDÔMEN
        Exercise(
            "plank_standard",
            "Prancha Abdominal",
            "CORE",
            "INICIANTE",
            "Nenhum",
            "3 séries x 45 segs",
            "Apoie os antebraços e as pontas dos pés no chão. Mantenha o corpo reto como uma tábua, contraindo o abdômen e glúteos.",
            "Não deixe o quadril cair ou levantar demais.",
            "plank"
        ),
        Exercise(
            "leg_raises_hanging",
            "Elevação de Pernas na Barra",
            "CORE",
            "INTERMEDIÁRIO",
            "Barra",
            "4 séries x 10 reps",
            "Pendurado na barra com os braços estendidos, contraia o abdômen e eleve as pernas totalmente estendidas até ficarem paralelas ao chão.",
            "Evite balançar. Controle a descida para máxima queima muscular.",
            "raises"
        ),
        // PEITO ADICIONAIS
        Exercise(
            "pushup_diamond",
            "Flexão Diamante",
            "PEITO",
            "INTERMEDIÁRIO",
            "Nenhum",
            "4 séries x 10 reps",
            "Aproxime as mãos de forma que os polegares e indicadores formem o desenho de um diamante/triângulo sob o peito. Execute a flexão mantendo os cotovelos fechados.",
            "Excelente variação para transferir a tensão para a porção interna do peitoral e tríceps.",
            "pushup"
        ),
        Exercise(
            "pushup_archer",
            "Flexão Arqueiro (Archer)",
            "PEITO",
            "AVANÇADO",
            "Nenhum",
            "3 séries x 6 reps de cada lado",
            "Abra bem os braços. Desça o corpo direcionando o peso totalmente sobre uma das mãos, mantendo o outro braço totalmente esticado. Alterne os lados.",
            "Serve como uma progressão fantástica para a flexão de um braço só.",
            "pushup"
        ),
        // COSTAS ADICIONAIS
        Exercise(
            "inverted_row",
            "Remada Invertida (Australian)",
            "COSTAS",
            "INICIANTE",
            "Barra",
            "4 séries x 12 reps",
            "Posicione-se embaixo de uma barra baixa (altura do quadril). Segure-a com pegada pronada, corpo reto e calcanhares apoiados. Puxe o peito até a barra.",
            "Mantenha a escápulas retraídas durante todo o percurso para trabalhar a densidade das costas.",
            "row"
        ),
        // OMBROS ADICIONAIS
        Exercise(
            "handstand_hold",
            "Parada de Mão na Parede (Hold)",
            "OMBROS",
            "AVANÇADO",
            "Nenhum",
            "3 séries x 30 segs",
            "Apoie as mãos a 15-20cm de uma parede resistente, chute as pernas para cima para ficar de ponta-cabeça com as costas voltadas para a parede. Mantenha os ombros empurrados ativos.",
            "Essencial para construir a força estática dos ombros e o alinhamento corporal.",
            "pike"
        ),
        // BRAÇOS ADICIONAIS
        Exercise(
            "tricep_extensions",
            "Extensão de Tríceps no Banco",
            "BRAÇOS",
            "INTERMEDIÁRIO",
            "Banco/Cadeira",
            "3 séries x 12 reps",
            "Apoie as mãos na borda de um banco, dê passos atrás e flexione a testa em direção às mãos, dobrando apenas os cotovelos. Empurre esticando o tríceps.",
            "Foque em isolar o tríceps sem deixar o quadril ou as costas balançarem.",
            "dips"
        ),
        Exercise(
            "backpack_curl",
            "Rosca Concentrada com Mochila",
            "BRAÇOS",
            "INICIANTE",
            "Mochila",
            "3 séries x 15 reps",
            "Sentado, segure a alça da mochila com uma das mãos, apoie o cotovelo na parte interna da coxa correspondente e flexione o bíceps de forma isolada.",
            "Perfeito para trabalhar o pico do bíceps individualmente.",
            "row"
        ),
        // PERNAS ADICIONAIS
        Exercise(
            "bulgarian_split_squat",
            "Agachamento Búlgaro",
            "PERNAS",
            "INTERMEDIÁRIO",
            "Banco/Cadeira",
            "3 séries x 10 reps de cada lado",
            "Coloque um pé para trás apoiado no banco. Com o outro pé à frente, execute um agachamento profundo unilateral até o joelho traseiro quase tocar o chão.",
            "Destrói quadríceps e glúteos ao mesmo tempo que corrige assimetrias de força nas pernas.",
            "pistol"
        ),
        Exercise(
            "calf_raises",
            "Elevação de Panturrilha Unilateral",
            "PERNAS",
            "INICIANTE",
            "Nenhum",
            "4 séries x 20 reps de cada lado",
            "Fique em pé sobre um pé só (pode apoiar a mão em uma parede para equilíbrio). Suba na ponta do pé o máximo possível e desça controladamente.",
            "A amplitude completa e a descida controlada trarão resultados superiores para as panturrilhas.",
            "squat"
        ),
        // CORE ADICIONAIS
        Exercise(
            "v_ups",
            "Abdominal Canivete (V-Ups)",
            "CORE",
            "INTERMEDIÁRIO",
            "Nenhum",
            "3 séries x 12 reps",
            "Deite-se de costas, pernas e braços estendidos. Em um movimento explosivo, dobre o tronco e levante as pernas para tocar os pés no ar.",
            "Controle tanto a subida explosiva quanto a descida para maior estímulo do reto abdominal.",
            "plank"
        ),
        Exercise(
            "l_sit_hold",
            "L-Sit Isométrico no Solo",
            "CORE",
            "AVANÇADO",
            "Nenhum",
            "3 séries x 15 segs",
            "Sente-se com as pernas esticadas à frente. Coloque as mãos ao lado do quadril e empurre o chão para elevar todo o quadril e pernas do solo.",
            "Exige força extrema de compressão abdominal, flexores do quadril e tríceps.",
            "raises"
        ),
        // CARDIO / ESTAMINA ADICIONAIS
        Exercise(
            "burpees",
            "Burpees de Combate",
            "CORE",
            "INTERMEDIÁRIO",
            "Nenhum",
            "3 séries x 12 reps",
            "Comece em pé, agache, chute os pés para trás em posição de flexão, realize uma flexão, puxe os pés de volta e salte levantando as mãos.",
            "O exercício definitivo de corpo inteiro para condicionamento metabólico e queima de gordura.",
            "squat"
        )
    )

    val workouts = listOf(
        Workout(
            "beginner_no_equip",
            "Protocolo Recruta: Sem Equipamento",
            "Treino inicial focado nos fundamentos do controle de peso corporal. Ideal para despertar o sistema muscular.",
            "MOBILIDADE / FORÇA",
            "INICIANTE",
            15,
            120,
            50,
            "Nenhum",
            listOf(
                exercises.first { it.id == "squat_standard" },
                exercises.first { it.id == "pushup_standard" },
                exercises.first { it.id == "bench_dips" },
                exercises.first { it.id == "plank_standard" }
            )
        ),
        Workout(
            "chest_shoulder_beast",
            "Caminho do Guerreiro: Peito e Ombro",
            "Destruição completa de peito, ombros e tríceps utilizando apenas o peso corporal.",
            "PEITO / OMBROS",
            "INTERMEDIÁRIO",
            25,
            200,
            90,
            "Banco/Cadeira",
            listOf(
                exercises.first { it.id == "pushup_standard" },
                exercises.first { it.id == "pike_pushup" },
                exercises.first { it.id == "pushup_decline" },
                exercises.first { it.id == "bench_dips" }
            )
        ),
        Workout(
            "back_biceps_monarch",
            "Asas de Monarca: Costas e Bíceps",
            "Treino completo focado na barra livre e variações para construir dorsais largas e bíceps de aço.",
            "COSTAS / BRAÇOS",
            "INTERMEDIÁRIO",
            30,
            250,
            120,
            "Barra",
            listOf(
                exercises.first { it.id == "pullup_standard" },
                exercises.first { it.id == "chinup" },
                exercises.first { it.id == "backpack_row" },
                exercises.first { it.id == "leg_raises_hanging" }
            )
        ),
        Workout(
            "leg_day_titan",
            "Sobrecarga de Pernas Titan",
            "Treino desafiador para desenvolver força e hipertrofia explosiva nos membros inferiores.",
            "PERNAS",
            "AVANÇADO",
            35,
            350,
            180,
            "Nenhum",
            listOf(
                exercises.first { it.id == "pistol_squat" },
                exercises.first { it.id == "squat_standard" },
                exercises.first { it.id == "plank_standard" }
            )
        ),
        Workout(
            "full_gymnastic_rings",
            "Estabilidade Divina nas Argolas",
            "Treino avançado para atletas que buscam desenvolver o físico ultra-estabilizado de ginasta olímpico.",
            "PEITO / CORE / BRAÇOS",
            "AVANÇADO",
            25,
            400,
            250,
            "Argolas",
            listOf(
                exercises.first { it.id == "ring_dips" },
                exercises.first { it.id == "pushup_standard" },
                exercises.first { it.id == "leg_raises_hanging" }
            )
        ),
        Workout(
            "stamina_booster",
            "Caldeirão de Estamina: Cardio & Resistência",
            "Um protocolo dinâmico de alta intensidade feito para expandir sua capacidade pulmonar (STA) e derreter gordura.",
            "CARDIO / CORE",
            "INTERMEDIÁRIO",
            20,
            220,
            100,
            "Nenhum",
            listOf(
                exercises.first { it.id == "burpees" },
                exercises.first { it.id == "squat_standard" },
                exercises.first { it.id == "v_ups" },
                exercises.first { it.id == "plank_standard" }
            )
        ),
        Workout(
            "shoulder_tricep_fortress",
            "Cidadela Invisível: Ombros e Tríceps",
            "Desenvolva ombros fortes de guerreiro e tríceps densos através do controle estático avançado e isolamentos corporais.",
            "OMBROS / BRAÇOS",
            "AVANÇADO",
            30,
            380,
            200,
            "Banco/Cadeira",
            listOf(
                exercises.first { it.id == "handstand_hold" },
                exercises.first { it.id == "pike_pushup" },
                exercises.first { it.id == "pushup_diamond" },
                exercises.first { it.id == "tricep_extensions" }
            )
        ),
        Workout(
            "centaur_legs",
            "Protocolo Centauro: Hipertrofia de Pernas",
            "Combinação ideal de agachamentos búlgaros unilaterais e elevações para esculpir pernas simétricas e panturrilhas blindadas.",
            "PERNAS",
            "INTERMEDIÁRIO",
            25,
            240,
            110,
            "Banco/Cadeira",
            listOf(
                exercises.first { it.id == "bulgarian_split_squat" },
                exercises.first { it.id == "squat_standard" },
                exercises.first { it.id == "calf_raises" }
            )
        )
    )
}
