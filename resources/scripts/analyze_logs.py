import os
import re
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

def parse_time_log(file_path):
    pattern = r"(\d{2}/\d{2}/\d{4} \d{2}:\d{2}:\d{2}:\d{4}) INFO .* - (.*)"
    timestamps = {}

    with open(file_path, 'r') as file:
        for line in file:
            match = re.search(pattern, line)
            if match:
                timestamp = pd.to_datetime(match.group(1), format='%d/%m/%Y %H:%M:%S:%f')
                event = match.group(2)
                timestamps[event] = timestamp

    return timestamps

def calculate_durations(timestamps):
    # Definir los eventos esperados
    expected_events = [
        'Inicio de la lectura del archivo',
        'Fin de lectura del archivo',
        'Inicio del trabajo map/reduce',
        'Fin del trabajo map/reduce'
    ]

    # Verificar si todos los eventos están presentes
    for event in expected_events:
        if event not in timestamps:
            print(f"Advertencia: El evento '{event}' no se encontró en los logs.")
            return None

    durations = {
        'read_duration': (timestamps['Fin de lectura del archivo'] - timestamps['Inicio de la lectura del archivo']).total_seconds(),
        'mapreduce_duration': (timestamps['Fin del trabajo map/reduce'] - timestamps['Inicio del trabajo map/reduce']).total_seconds(),
        'total_duration': (timestamps['Fin del trabajo map/reduce'] - timestamps['Inicio de la lectura del archivo']).total_seconds()
    }
    return durations

def gather_metrics(log_dir, label):
    metrics = []

    for file_name in os.listdir(log_dir):
        if file_name.startswith('time') and file_name.endswith('.txt'):
            parts = re.split(r'[_\.]', file_name)
            query_num = int(re.search(r'time(\d+)', parts[0]).group(1))
            city = parts[1]
            repetition = int(parts[2])
            file_path = os.path.join(log_dir, file_name)
            timestamps = parse_time_log(file_path)
            durations = calculate_durations(timestamps)
            if durations:
                durations['query'] = query_num
                durations['city'] = city
                durations['repetition'] = repetition
                durations['label'] = label
                metrics.append(durations)

    return pd.DataFrame(metrics)

def plot_metrics(metrics_df):
    fig, axes = plt.subplots(3, 1, figsize=(10, 15))

    sns.barplot(x='query', y='read_duration', hue='label', data=metrics_df, ax=axes[0])
    axes[0].set_title('Tiempos de Lectura de Archivos por Query')
    axes[0].set_xlabel('Query')
    axes[0].set_ylabel('Duración (s)')

    sns.barplot(x='query', y='mapreduce_duration', hue='label', data=metrics_df, ax=axes[1])
    axes[1].set_title('Tiempos de Ejecución de MapReduce por Query')
    axes[1].set_xlabel('Query')
    axes[1].set_ylabel('Duración (s)')

    sns.barplot(x='query', y='total_duration', hue='label', data=metrics_df, ax=axes[2])
    axes[2].set_title('Tiempos Totales por Query')
    axes[2].set_xlabel('Query')
    axes[2].set_ylabel('Duración (s)')

    plt.tight_layout()
    plt.show()

    # Graficar mapa de calor de los tiempos
    heatmap_data = metrics_df.pivot_table(index='query', columns=['repetition', 'label'], values='total_duration')
    plt.figure(figsize=(12, 8))
    sns.heatmap(heatmap_data, annot=True, fmt=".1f", cmap="YlGnBu")
    plt.title('Mapa de Calor de Duraciones Totales')
    plt.xlabel('Repetición y Etiqueta')
    plt.ylabel('Query')
    plt.show()

def plot_metrics_by_city(metrics_df):
    cities = metrics_df['city'].unique()

    for city in cities:
        city_df = metrics_df[metrics_df['city'] == city]
        fig, axes = plt.subplots(3, 1, figsize=(10, 15))

        sns.barplot(x='query', y='read_duration', hue='label', data=city_df, ax=axes[0])
        axes[0].set_title(f'Tiempos de Lectura de Archivos por Query ({city})')
        axes[0].set_xlabel('Query')
        axes[0].set_ylabel('Duración (s)')

        sns.barplot(x='query', y='mapreduce_duration', hue='label', data=city_df, ax=axes[1])
        axes[1].set_title(f'Tiempos de Ejecución de MapReduce por Query ({city})')
        axes[1].set_xlabel('Query')
        axes[1].set_ylabel('Duración (s)')

        sns.barplot(x='query', y='total_duration', hue='label', data=city_df, ax=axes[2])
        axes[2].set_title(f'Tiempos Totales por Query ({city})')
        axes[2].set_xlabel('Query')
        axes[2].set_ylabel('Duración (s)')

        plt.tight_layout()
        plt.show()

        # Graficar mapa de calor de los tiempos por ciudad
        heatmap_data = city_df.pivot_table(index='query', columns=['repetition', 'label'], values='total_duration')
        plt.figure(figsize=(12, 8))
        sns.heatmap(heatmap_data, annot=True, fmt=".1f", cmap="YlGnBu")
        plt.title(f'Mapa de Calor de Duraciones Totales ({city})')
        plt.xlabel('Repetición y Etiqueta')
        plt.ylabel('Query')
        plt.show()

def additional_statistics(metrics_df):
    # Estadísticas básicas
    stats = metrics_df.groupby(['query', 'label']).agg({
        'read_duration': ['mean', 'median', 'std'],
        'mapreduce_duration': ['mean', 'median', 'std'],
        'total_duration': ['mean', 'median', 'std']
    }).reset_index()
    print("Estadísticas básicas por query y etiqueta:")
    print(stats)

    # Histogramas de distribuciones
    fig, axes = plt.subplots(3, 1, figsize=(10, 15))

    sns.histplot(metrics_df, x='read_duration', hue='label', bins=10, kde=True, ax=axes[0])
    axes[0].set_title('Distribución de Duraciones de Lectura')
    axes[0].set_xlabel('Duración de Lectura (s)')
    axes[0].set_ylabel('Frecuencia')

    sns.histplot(metrics_df, x='mapreduce_duration', hue='label', bins=10, kde=True, ax=axes[1])
    axes[1].set_title('Distribución de Duraciones de MapReduce')
    axes[1].set_xlabel('Duración de MapReduce (s)')
    axes[1].set_ylabel('Frecuencia')

    sns.histplot(metrics_df, x='total_duration', hue='label', bins=10, kde=True, ax=axes[2])
    axes[2].set_title('Distribución de Duraciones Totales')
    axes[2].set_xlabel('Duración Total (s)')
    axes[2].set_ylabel('Frecuencia')

    plt.tight_layout()
    plt.show()

    # Box plots para comparar duraciones
    fig, axes = plt.subplots(3, 1, figsize=(10, 15))

    sns.boxplot(x='query', y='read_duration', hue='label', data=metrics_df, ax=axes[0])
    axes[0].set_title('Comparación de Duraciones de Lectura por Query')
    axes[0].set_xlabel('Query')
    axes[0].set_ylabel('Duración de Lectura (s)')

    sns.boxplot(x='query', y='mapreduce_duration', hue='label', data=metrics_df, ax=axes[1])
    axes[1].set_title('Comparación de Duraciones de MapReduce por Query')
    axes[1].set_xlabel('Query')
    axes[1].set_ylabel('Duración de MapReduce (s)')

    sns.boxplot(x='query', y='total_duration', hue='label', data=metrics_df, ax=axes[2])
    axes[2].set_title('Comparación de Duraciones Totales por Query')
    axes[2].set_xlabel('Query')
    axes[2].set_ylabel('Duración Total (s)')

    plt.tight_layout()
    plt.show()

if __name__ == "__main__":
    log_directory_1 = "../outputs/"  # Cambia esta ruta a la ubicación de tu primer conjunto de archivos de logs
    log_directory_2 = "../outputs2/"  # Cambia esta ruta a la ubicación de tu segundo conjunto de archivos de logs

    metrics_df_1 = gather_metrics(log_directory_1, 'original')
    metrics_df_2 = gather_metrics(log_directory_2, 'modified')

    metrics_df = pd.concat([metrics_df_1, metrics_df_2], ignore_index=True)

    if not metrics_df.empty:
        plot_metrics(metrics_df)
        plot_metrics_by_city(metrics_df)
        additional_statistics(metrics_df)
    else:
        print("No se encontraron métricas válidas para analizar.")
